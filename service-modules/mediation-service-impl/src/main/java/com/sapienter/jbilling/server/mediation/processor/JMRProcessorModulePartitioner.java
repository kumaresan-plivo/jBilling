package com.sapienter.jbilling.server.mediation.processor;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.mediation.JbillingMediationRecord;
import com.sapienter.jbilling.server.mediation.converter.db.DaoConverter;
import com.sapienter.jbilling.server.mediation.converter.db.JMRRepository;
import com.sapienter.jbilling.server.mediation.converter.db.JbillingMediationRecordDao;
import org.apache.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creates execution context for remote processors.
 *
 * @author Gerhard Maree
 * @since 29-07-2015
 */
public class JMRProcessorModulePartitioner implements Partitioner, JmrProcessorConstants {

    @Autowired
    private JMRRepository jmrRepository;

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(JMRProcessorModulePartitioner.class));

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> contextMap = new HashMap<>(gridSize*2);
        List<JbillingMediationRecord> unprocessedMediationRecords =
                jmrRepository.findByStatus(JbillingMediationRecordDao.STATUS.UNPROCESSED)
                        .stream()
                        .map(DaoConverter::getMediationRecord)
                        .collect(Collectors.toList());

        //TODO Should retrieve jmr records by process id count...
        long jmrQuantity = unprocessedMediationRecords.size();
        List<Integer> userIds = unprocessedMediationRecords.stream().map(r -> r.getUserId())
                .distinct().collect(Collectors.toList());
        int numberOfUsersForProcessor = userIds.size() / gridSize + 1;
        Iterator<Integer> userIterator = userIds.iterator();
        int userCounterInProcessor = 0;
        int processorIdx = 0;
        String usersCommaSeparatedString = "";
        while(userIterator.hasNext()) {
            if (userCounterInProcessor == numberOfUsersForProcessor) {
                createContextForUsers(gridSize, contextMap, processorIdx, usersCommaSeparatedString);
                usersCommaSeparatedString = "";
                userCounterInProcessor = 0;
                processorIdx++;
            }
            usersCommaSeparatedString += "," + userIterator.next();
            userCounterInProcessor++;
        }
        if (!usersCommaSeparatedString.isEmpty()) {
            createContextForUsers(gridSize, contextMap, processorIdx, usersCommaSeparatedString);
        }
        LOG.debug("jmrQuantity=%s, gridSize=%s", jmrQuantity, gridSize);
        return contextMap;
    }

    private void createContextForUsers(int gridSize, Map<String, ExecutionContext> contextMap, int processorIdx, String usersCommaSeparatedString) {
        Map<String, Object> parameters = new HashMap(4);
        parameters.put(PARM_PROCESSOR_IDX, processorIdx);
        parameters.put(PARM_GRID_SIZE, gridSize);
        parameters.put(PARM_USER_LIST, usersCommaSeparatedString);

        LOG.debug("Created partition %s with parameters %s", processorIdx, parameters);

        ExecutionContext ctx = new ExecutionContext(parameters);
        contextMap.put("DefaultPartitionedJMRProcessorStep:partition"+processorIdx, ctx);
    }

}
