package org.sai.rts.streamsearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by saipkri on 25/10/16.
 */
public class Scratchpad {

    public static void main(String[] args) throws Exception {

        ExecutorService tp = Executors.newFixedThreadPool(8);

        tp.submit(() -> {
            while (true) {
                System.out.println("Im submitted");
            }
        });
        tp.shutdownNow();

    }
}
