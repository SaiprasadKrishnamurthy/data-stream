package org.sai.audit.meta;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sai.audit.meta.model.EventConfig;

import java.util.List;

/**
 * Created by saipkri on 24/10/16.
 */
public class Scratchpad {

    public static void main(String[] args) throws Exception {

        ObjectMapper m = new ObjectMapper();
        List<EventConfig> configs = m.readValue(Scratchpad.class.getClassLoader().getResourceAsStream("Configs.json"), List.class);
        System.out.println(configs);


    }
}
