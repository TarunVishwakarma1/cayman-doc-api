package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.model.CabinetProperties;
import com.newgen.dmsapi.DMSCallBroker;
import com.newgen.dmsapi.DMSXmlResponse;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Operations {

    private static final Logger LOG = Logger.getLogger(Operations.class);

    @Autowired
    private CabinetProperties cabinet;

    public String getValueFromXML(String xml, String key){
        LOG.debug("Parameters provided in "+this.getClass().getName()+" : XML: "+xml+", key: "+key);
        DMSXmlResponse xmlResponse = new DMSXmlResponse(xml);
        String value = xmlResponse.getVal(key);
        LOG.debug("Value extracted from parameters: "+ value);
        return value;
    }

    public String executeXML(String xml) throws Exception {
        LOG.debug("XML to be executed: "+xml);
        LOG.info("//---------- Executing XML ----------//");
        try {
            String outXML = DMSCallBroker.execute(xml, cabinet.getJtsIP(), Integer.parseInt(cabinet.getJtsPort()), 1);
            LOG.debug("XML After Executing: " + outXML);
            LOG.info("//---------- XML Executed Successfully ----------//");
            return outXML;
        }catch(Exception e){
            LOG.error(e);
            throw e;
        }
    }

}
