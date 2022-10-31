package com.yar.iotpipeline.domain;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The record document is contained the message data to store into database
 * */
@Document("RecordDocument")
public class RecordDocument extends HashMap<String, Object> {

    public RecordDocument(Map<? extends String, ?> m) {
        super(m);
    }

    public RecordDocument() {
        super(new HashMap<>());
    }
}
