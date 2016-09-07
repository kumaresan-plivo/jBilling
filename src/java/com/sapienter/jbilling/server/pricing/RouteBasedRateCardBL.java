package com.sapienter.jbilling.server.pricing;

import au.com.bytecode.opencsv.CSVReader;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.pricing.db.RatingUnitDAS;
import com.sapienter.jbilling.server.pricing.db.RouteRateCardDAS;
import com.sapienter.jbilling.server.pricing.db.RouteRateCardDTO;
import com.sapienter.jbilling.server.user.RouteRateCardWS;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.*;
import com.sapienter.jbilling.server.util.Context;
import com.sapienter.jbilling.server.util.sql.JDBCUtils;
import com.sapienter.jbilling.server.util.sql.TableGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RouteBasedRateCardBL {
    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(RouteBasedRateCardBL.class));

    public static final int BATCH_SIZE = 10;
    public static final String DEFAULT_DATA_TYPE = "varchar(255)";

    private RouteRateCardDAS routeRateCardDAS;
    private JdbcTemplate jdbcTemplate;
    private TableGenerator tableGenerator;

    private RouteRateCardDTO routeRateCardDTO;

    public RouteBasedRateCardBL() {
        _init();
    }

    public RouteBasedRateCardBL(Integer routeId) {
        _init();
        set(routeId);
    }

    public RouteBasedRateCardBL(RouteRateCardDTO routeRateCardDTO) {
        _init();
        this.routeRateCardDTO = routeRateCardDTO;
        this.tableGenerator = new TableGenerator(routeRateCardDTO.getTableName(), RouteRateCardDTO.TABLE_COLUMNS);
    }

    public void set(Integer routeRateId) {
        this.routeRateCardDTO = routeRateCardDAS.find(routeRateId);
        this.tableGenerator = new TableGenerator(routeRateCardDTO.getTableName(), RouteRateCardDTO.TABLE_COLUMNS);
    }

    public RouteRateCardWS getWS() {
        RouteRateCardWS ws = new RouteRateCardWS();
        ws.setId(routeRateCardDTO.getId());
        ws.setName(routeRateCardDTO.getName());
        ws.setTableName(routeRateCardDTO.getTableName());
        ws.setEntityId(routeRateCardDTO.getCompany().getId());
        ws.setRatingUnitId(routeRateCardDTO.getRatingUnit().getId());
        return ws;
    }

    public static final RouteRateCardDTO getRouteRateCardDTO(RouteRateCardWS ws,Integer entityId) {
        RouteRateCardDTO routeRateCardDTO = new RouteRateCardDTO();
        routeRateCardDTO.setId(ws.getId());
        routeRateCardDTO.setName(ws.getName());
        routeRateCardDTO.setTableName(ws.getTableName());
        if (ws.getRatingUnitId() != null) {
            routeRateCardDTO.setRatingUnit(new RatingUnitDAS().find(ws.getRatingUnitId()));
        } else {
            routeRateCardDTO.setRatingUnit(RatingUnitBL.getDefaultRatingUnit(entityId));
        }

        return routeRateCardDTO;
    }

    /**
     * Create a new route rate card with csv
     * @param routeRateCardDTO route rate card for the entity
     * @param routeFile file handle of the CSV on disk containing the route rate card.
     * @return
     */
    public Integer create(RouteRateCardDTO routeRateCardDTO, File routeFile) {

        if (routeRateCardDTO != null) {
            LOG.debug("Saving new route rate card " + routeRateCardDTO);
            this.routeRateCardDTO = routeRateCardDAS.save(routeRateCardDTO);


            this.tableGenerator = new TableGenerator(this.routeRateCardDTO.getTableName(), RouteRateCardDTO.TABLE_COLUMNS);
            LOG.debug("Creating a new  route rate table & saving rating data");
            if (routeFile != null) {
                try {
                    checkRouteTableExistence(this.routeRateCardDTO.getTableName());
                    saveRoutes(routeFile);

                } catch (SessionInternalError e) {
                    dropRoutes();
                    throw e;
                } catch (IOException e) {
                    dropRoutes();

                    throw new SessionInternalError("Could not load rating table", e, new String[] { "RouteWS,routes,cannot.read.file" });
                } catch (SQLException e) {
                    dropRoutes();
                    throw new SessionInternalError("Exception saving rates to database", e, new String[] { "RouteWS,routes,cannot.save.rates.db.error" });
                }

                registerSpringBeans();
            }

            return this.routeRateCardDTO.getId();
        }

        LOG.error("Cannot save a null RateCardDTO!");
        return null;
    }
    private void _init() {
        this.routeRateCardDAS = new RouteRateCardDAS();
        this.jdbcTemplate = Context.getBean(Context.Name.JDBC_TEMPLATE);
    }

    private void checkRouteTableExistence(String tableName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        List<String> tableNames = JDBCUtils.getAllTableNames(connection);
        if (tableNames.contains(tableName.toLowerCase())) {
            String dropTable = tableGenerator.buildDropTableSQL();
            jdbcTemplate.execute(dropTable);
        }
    }

    /**
     * Updates the route table with the route information in
     * the given CSV
     *
     * @param routesFile file handle of the CSV on disk containing the route.
     * @throws IOException if file does not exist or is not readable
     */
    public void saveRoutes(File routesFile) throws IOException, SQLException {

        CSVReader reader = new CSVReader(new FileReader(routesFile));
        String[] line = reader.readNext();
        validateCsvHeader(line);

        // parse the header and read out the extra columns.
        int start = routeRateCardDTO.TABLE_COLUMNS.size();
        for (int i = start; i <line.length; i++)
        {
            System.out.println("Adding Column to table"+line[i]);
            tableGenerator.addColumn(new TableGenerator.Column(line[i], DEFAULT_DATA_TYPE, true));
        }

        // create route table
        String createSql = tableGenerator.buildCreateTableSQL();
        System.out.println("CreateSQL"+createSql);
        jdbcTemplate.execute(createSql);

        LOG.debug("Created table '" + routeRateCardDTO.getTableName() + "'");

        // load rating data in batches
        String insertSql = tableGenerator.buildInsertPreparedStatementSQL();
        List<List<String>> rows = new ArrayList<List<String>>();
        for (int i = 1; i <= BATCH_SIZE; i++) {
            // add row to insert batch
            line = reader.readNext();
            if (line != null) {
                // Handle free and non applicable pricing terms as extra column
                List<String> currentLine = new ArrayList<String>(Arrays.asList(line));
                rows.add(currentLine);
            }

            // end of file
            if (line == null) {
                executeBatchInsert(insertSql, rows);
                break; // done
            }

            // reached batch limit
            if (i == BATCH_SIZE) {
                executeBatchInsert(insertSql, rows);
                i = 1; rows.clear(); // next batch
            }
        }
    }
    /**
     * Validates that the uploaded CSV file starts with the expected columns from {@link RouteRateCardDTO#TABLE_COLUMNS}.
     * If the column names don't match or are in an incorrect order a SessionInternalError will be throw.
     *
     * @param header header line to validate
     * @throws SessionInternalError thrown if errors found in header data
     */
    private void validateCsvHeader(String[] header) throws SessionInternalError {
        List<String> errors = new ArrayList<String>();

        List<TableGenerator.Column> columns = RouteRateCardDTO.TABLE_COLUMNS;
        for (int i = 0; i < columns.size(); i++) {
            if (!"price_term".equals(columns.get(i).getName())) {
                String columnName = header[i].trim();
                String expected = columns.get(i).getName();

                if (!expected.equalsIgnoreCase(columnName)) {
                    errors.add("RouteWS,routes,route.unexpected.header.value," + expected + "," + columnName);
                }
            }
        }

        if (!errors.isEmpty()) {
            System.out.println("throw new SessionInternalError Route CSV has errors in columns, or is missing required columns");
            throw new SessionInternalError("Route CSV has errors in  columns, or is missing required columns",
                    errors.toArray(new String[errors.size()]));
        }
    }

    /**
     * Inserts a batch of records into the database.
     *
     * @param insertSql prepared statement SQL
     * @param rows list of rows to insert
     */
    private void executeBatchInsert(String insertSql, final List<List<String>> rows) {
        LOG.debug("Inserting " + rows.size() + " records:");
        LOG.debug(rows);
        jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement preparedStatement, int batch) throws SQLException {
                List<String> values = rows.get(batch);
                for (int i = 0; i < values.size(); i++) {
                    String value = values.get(i);

                    switch (i) {
                        case 0:  // row id
                            preparedStatement.setInt(i + 1, StringUtils.isNotBlank(value) ? Integer.valueOf(value) : 0);
                            break;


                        default: // everything else
                            preparedStatement.setObject(i + 1, value);
                    }
                }
            }

            public int getBatchSize() {
                return rows.size();
            }
        });
    }

    /**
     * Drop the route table
     */
    public void dropRoutes() {
        String dropSql = tableGenerator.buildDropTableSQL();
        jdbcTemplate.execute(dropSql);
        LOG.debug("Dropped table '" + routeRateCardDTO.getTableName() + "'");
    }

    /*
            Spring Beans stuff
     */

    public void registerSpringBeans() {
        registerSpringBeans(false);
    }

    /**
     * Registers spring beans with the application context so support caching and look-up
     * of pricing from the rating tables.
     */
    public void registerSpringBeans(boolean finderOnly) {

        // register spring beans!
        GenericApplicationContext ctx = (GenericApplicationContext) Context.getApplicationContext();
        RouteRateCardBeanFactory factory = getBeanFactory();

        if (finderOnly) {
            String finderBeanName = factory.getFinderBeanName();
            BeanDefinition finderBeanDef = factory.getFinderBeanDefinition(null);

            LOG.info("Registering beans: " + finderBeanName);
            ctx.registerBeanDefinition(finderBeanName, finderBeanDef);
        } else {

            String readerBeanName = factory.getReaderBeanName();
            BeanDefinition readerBeanDef = factory.getReaderBeanDefinition(routeRateCardDTO.getCompany().getId());

            String loaderBeanName = factory.getLoaderBeanName();
            BeanDefinition loaderBeanDef = factory.getLoaderBeanDefinition(readerBeanName);

            String finderBeanName = factory.getFinderBeanName();
            BeanDefinition finderBeanDef = factory.getFinderBeanDefinition(loaderBeanName);

            String tableDefBeanName = factory.getTableDescriptorBeanName();
            BeanDefinition tableDefBeanDef = factory.getTableDescriptorBeanDefinition();

            Map<String, BeanDefinition> updateBeans = factory.getRouteRateCardUpdaterDependentBeanDefinitions();
            String updaterBeanName = factory.getRouteRateCardUpdaterBeanName();
            BeanDefinition updaterBeanDef = factory.getRouteRateCardUpdaterAggregateBeanDefinitions(updateBeans.keySet());
            LOG.info("Registering beans: " + readerBeanName + ", " + loaderBeanName + ", " + finderBeanName+", "+updaterBeanName);

            ctx.registerBeanDefinition(readerBeanName, readerBeanDef);
            ctx.registerBeanDefinition(loaderBeanName, loaderBeanDef);
            ctx.registerBeanDefinition(finderBeanName, finderBeanDef);
            ctx.registerBeanDefinition(tableDefBeanName, tableDefBeanDef);
            for (String beanName : updateBeans.keySet()) {
                ctx.registerBeanDefinition(beanName, updateBeans.get(beanName));
            }
            ctx.registerBeanDefinition(updaterBeanName, updaterBeanDef);

            //the init method doesn't get called when registering a bean. Force loading of the cache
            factory.getLoaderInstance();
        }
    }

    public void removeSpringBeans() {
        removeSpringBeans(false);
    }

    /**
     * Removes registered spring beans from the application context.
     */
    public void removeSpringBeans(boolean finderOnly) {
        RouteRateCardBeanFactory factory = getBeanFactory();

        try {
            GenericApplicationContext ctx = (GenericApplicationContext) Context.getApplicationContext();
            String finderBeanName = factory.getFinderBeanName();

            if (finderOnly) {
                LOG.info("Removing beans: " + finderBeanName);
                ctx.removeBeanDefinition(finderBeanName);
            } else {
                String readerBeanName = factory.getReaderBeanName();
                String loaderBeanName = factory.getLoaderBeanName();

                LOG.info("Removing beans: " + readerBeanName + ", " + loaderBeanName + ", " + finderBeanName);

                ctx.removeBeanDefinition(finderBeanName);
                ctx.removeBeanDefinition(readerBeanName);
                ctx.removeBeanDefinition(loaderBeanName);
            }

        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("Beans not found");
        }
    }


    /**
     * Returns an instance of the {@link RouteBeanFactory}
     * @return route bean factory
     */
    public RouteRateCardBeanFactory getBeanFactory() {
        return new RouteRateCardBeanFactory(routeRateCardDTO);
    }

    /**
     * Returns a list of column names read from the route table in the database.
     * @return column names
     */
    public List<String> getRouteTableColumnNames() {
        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);

        List<String> columns = Collections.emptyList();

        try {
            columns = JDBCUtils.getAllColumnNames(connection, routeRateCardDTO.getTableName());
        } catch (SQLException e) {

            throw new SessionInternalError("Could not read columns from route table.", e,
                    new String[] { "RouteWS,routes,route.cannot.read.rating.table" });

        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return columns;
    }

    /**
     * Returns a list of column names read from the route table in the database.
     * @return column names
     */
    public List<String> getRouteTableColumnNames(String tableName) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);

        List<String> columns = Collections.emptyList();

        try {
            columns = JDBCUtils.getAllColumnNames(connection, tableName);
        } catch (SQLException e) {

            throw new SessionInternalError("Could not read columns from route table.", e,
                    new String[] { "RouteWS,routes,route.cannot.read.rating.table" });

        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return columns;
    }
    public ScrollableResults getRouteTableRows() {
        return routeRateCardDAS.getRouteRateCardTableRows(routeRateCardDTO.getTableName());
    }

    /**
     * Updates an existing rate card and rates.
     *
     * @param routeRateCardDTO route rate card to create
     * @param routeRatesFile file handle of the CSV on disk containing the route based rates.
     */
    public void update(RouteRateCardDTO routeRateCardDTO, File routeRatesFile) {
    	
        if (this.routeRateCardDTO != null) {
        	
        	routeRateCardDTO.getMatchingFields().clear();
        	
            // re-create the route based rating table
            LOG.debug("Re-creating the route rate table & saving updated route based rating data");
            if (routeRateCardDTO != null && routeRatesFile!=null) {
               dropRouteRates();

                try {
                    saveRoutes(routeRatesFile);

                } catch (IOException e) {
                    dropRouteRates();
                    throw new SessionInternalError("Could not load route based rating table", e, new String[] { "RouteRateCardWS,csv,cannot.read.file" });
                } catch (SQLException e) {
                    dropRouteRates();
                    throw new SessionInternalError("Exception saving rates to database", e, new String[] { "RouteRateCardWS,rates,cannot.save.rates.db.error" });
                }
            }

            // prepare SQL to rename the table if the table name has changed
            String originalTableName = this.routeRateCardDTO.getTableName();
            String alterTableSql = null;

            if (!originalTableName.equals(routeRateCardDTO.getTableName())) {
                try {

                    checkRouteTableExistence(routeRateCardDTO.getTableName());
                } catch (SQLException e) {
                    dropRouteRates();
                    throw new SessionInternalError("Exception saving rates to database", e,
                            new String[] { "RouteRateCardWS,csv,cannot.save.rates.db.error" });
                }
                alterTableSql = this.tableGenerator.buildRenameTableSQL(routeRateCardDTO.getTableName());
                //remove and re-register spring beans
                removeSpringBeans();
            }

            // do update
            this.routeRateCardDTO.setName(routeRateCardDTO.getName());
            if (!this.routeRateCardDTO.getTableName().equals(routeRateCardDTO.getTableName())) {
                this.routeRateCardDTO.setTableName(routeRateCardDTO.getTableName());
                registerSpringBeans();
            }

            // updating the rating unit
            this.routeRateCardDTO.setRatingUnit(routeRateCardDTO.getRatingUnit());

            System.out.println("RouteRate card dto before save "+routeRateCardDTO);
            LOG.debug("Saving updates to route rate card " + routeRateCardDTO.getId());
            this.routeRateCardDTO = routeRateCardDAS.save(this.routeRateCardDTO);
            this.tableGenerator = new TableGenerator(this.routeRateCardDTO.getTableName(), RouteRateCardDTO.TABLE_COLUMNS);

            // do rename after saving the new table name
            if (alterTableSql != null) {
                LOG.debug("Renaming the rate table");
                jdbcTemplate.execute(alterTableSql);
            }

            // re-register spring beans if rates were updated
            if (routeRatesFile != null) {
                removeSpringBeans();
                registerSpringBeans();
            }

        } else {
            LOG.error("Cannot update, RouteRateCardDTO not found or not set!");
        }
    }

    /**
     * Deletes the current rate card managed by this class.
     */
    public void delete() {

        if (routeRateCardDTO != null) {
            routeRateCardDAS.delete(routeRateCardDTO);

            dropRouteRates();
            routeRateCardDAS.flush();
        } else {
            LOG.error("Cannot delete, RouteRateCardDTO not found or not set!");
        }
    }

    /**
     * Drop the rate table of a rate card.
     */
    public void dropRouteRates() {
        String dropSql = tableGenerator.buildDropTableSQL();
        try {
        	jdbcTemplate.execute(dropSql);
        } catch (Exception e) {
        	LOG.error(e);
        } finally {
        	//
        }
        LOG.debug("Dropped table '" + routeRateCardDTO.getTableName() + "'");
    }

    /**
     *
     * @param entityId Entity Id of the company for which route is created
     * @param routeRateCardName Route rate card name
     * @return
     */
    public String createRouteRateCardTableName(Integer entityId,String routeRateCardName){
         return RouteRateCardDTO.TABLE_PREFIX+entityId+"_" + JDBCUtils.getDatabaseObjectName(routeRateCardName);
    }

    /**
     * Get List of unused additional columns added from route rate card csv
     * @param tableName Table name.
     * @param rateCardRouteId route rate card id
     * @return Unused Additional columns added from route rate card CSV.
     */
    public List<String> getUnusedRouteRateCardColumns(String tableName,Integer rateCardRouteId){
        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        List<String> tableColumns = Collections.emptyList();

        try {
        	tableColumns = JDBCUtils.getAllColumnNames(connection, tableName);
        } catch (Exception e) {
        	LOG.error(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        
        tableColumns.removeAll(RouteRateCardDTO.TABLE_COLUMNS_NAMES);
        tableColumns.removeAll(new MatchingFieldDAS().getRouteRateCardUsedMatchingFields(rateCardRouteId));
        return tableColumns;
    }

    /**
     * Get List of matching field column names for a route
     * @param routeRateCardId routeRateCardId for the entity
     * @return List of matching field for a route  based on required field
     */
    public List<String> getMatchingFieldColumns(Integer routeRateCardId){

        List<MatchingFieldDTO> matchingFieldDTOs = getMatchingFieldsByRouteRateCardId(routeRateCardId);
        List<String> columnNames = new ArrayList<String>();
        for (MatchingFieldDTO matchingFieldDTO:matchingFieldDTOs){
            columnNames.add(matchingFieldDTO.getMatchingField());
        }
        return columnNames;
    }

    /**
     * Get List of matching field DTO  for a route based on by routeId
     * @param routeRateCardId route RateCard Id for the entity
     * @return List of matching field based on required field
     */
    public List<MatchingFieldDTO> getMatchingFieldsByRouteRateCardId(Integer routeRateCardId){
        List<MatchingFieldDTO> matchingFieldDTOs = new MatchingFieldDAS().getMatchingFieldsByRouteRateCardId(routeRateCardId);
        return  matchingFieldDTOs;
    }

    public Integer getLongestValueFor(String matchingField) {
    	return routeRateCardDAS.getLongestValue(getEntity().getTableName(), matchingField);
    }
    
    public Integer getSmallestValueFor(String matchingField) {
    	return routeRateCardDAS.getSmallestValue(getEntity().getTableName(), matchingField);
    }
    
    public RouteRateCardDTO getEntity() {
    	return routeRateCardDTO;
    }
}
