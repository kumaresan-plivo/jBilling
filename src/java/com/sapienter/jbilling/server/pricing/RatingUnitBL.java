package com.sapienter.jbilling.server.pricing;

import com.sapienter.jbilling.common.FormatLogger;
import com.sapienter.jbilling.server.pricing.db.IncrementUnit;
import com.sapienter.jbilling.server.pricing.db.PriceUnit;
import com.sapienter.jbilling.server.pricing.db.RatingUnitDAS;
import com.sapienter.jbilling.server.pricing.db.RatingUnitDTO;
import com.sapienter.jbilling.server.user.db.CompanyDTO;

import org.apache.log4j.Logger;

/**
 *  Rating Unit BL
 *
 *  @author Panche Isajeski
 *  @since 27-Aug-2013
 */
public class RatingUnitBL {

    private static final FormatLogger LOG = new FormatLogger(Logger.getLogger(RatingUnitBL.class));

    private RatingUnitDTO ratingUnitDTO = null;
    private RatingUnitDAS ratingUnitDAS = null;

    private void init() {
        ratingUnitDAS = new RatingUnitDAS();
        ratingUnitDTO = new RatingUnitDTO();
    }

    public RatingUnitWS getWS() {
        return getWS(ratingUnitDTO);
    }
    public static final RatingUnitWS getWS(RatingUnitDTO dto){
    	
    	RatingUnitWS ws = new RatingUnitWS();
        ws.setId(dto.getId());
        ws.setEntityId(dto.getCompany().getId());
        ws.setName(dto.getName());
        ws.setPriceUnitName(dto.getPriceUnit().getName());
        ws.setIncrementUnitName(dto.getIncrementUnit().getName());
        ws.setIsCanBeDeleted(dto.isCanBeDeleted());
        ws.setIncrementUnitQuantityAsDecimal(dto.getIncrementUnit().getQuantity());
        return ws;
    }
    
    public static final RatingUnitDTO getDTO(RatingUnitWS ws,Integer entityId) {

        RatingUnitDTO ratingUnitDTO = new RatingUnitDTO();
        if (ws.getId() != null && ws.getId() > 0) {
            ratingUnitDTO.setId(ws.getId());
        }

        ratingUnitDTO.setCompany(new CompanyDTO(entityId));
        ratingUnitDTO.setName(ws.getName());

        PriceUnit priceUnit = new PriceUnit();
        priceUnit.setName(ws.getPriceUnitName());
        ratingUnitDTO.setPriceUnit(priceUnit);

        IncrementUnit incrementUnit = new IncrementUnit();
        incrementUnit.setName(ws.getIncrementUnitName());
        incrementUnit.setQuantity(ws.getIncrementUnitQuantityAsDecimal());
        ratingUnitDTO.setIncrementUnit(incrementUnit);

        return ratingUnitDTO;
    }

    public RatingUnitBL() {
        init();
    }

    public RatingUnitBL(Integer ratingUnitId) {
        init();
        setRatingUnit(ratingUnitId);
    }

    public void setRatingUnit(Integer ratingUnitId) {
        ratingUnitDTO = ratingUnitDAS.find(ratingUnitId);
    }

    public RatingUnitDTO getRatingUnit() {
        return ratingUnitDTO;
    }

    public boolean delete() {

        if (ratingUnitDTO.isCanBeDeleted()) {
            ratingUnitDAS.delete(ratingUnitDTO);
            return true;
        }

        return false;
    }

    public RatingUnitDTO create(RatingUnitDTO ratingUnitDTO) {

        ratingUnitDTO = ratingUnitDAS.save(ratingUnitDTO);

        ratingUnitDAS.flush();
        ratingUnitDAS.clear();
        return ratingUnitDTO;
    }

    public void update(RatingUnitDTO ratingUnit) {

        RatingUnitDTO ratingUnitDTO = ratingUnitDAS.find(ratingUnit.getId());

        ratingUnitDTO.setIncrementUnit(ratingUnit.getIncrementUnit());
        ratingUnitDTO.setPriceUnit(ratingUnit.getPriceUnit());
        ratingUnitDTO.setName(ratingUnit.getName());

        ratingUnitDAS.save(ratingUnitDTO);

        ratingUnitDAS.flush();
        ratingUnitDAS.clear();
    }

    public static RatingUnitDTO getDefaultRatingUnit(Integer entityId) {
        return new RatingUnitDAS().getDefaultRatingUnit(entityId);
    }
}
