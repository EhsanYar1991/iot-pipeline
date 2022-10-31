package com.yar.iotpipeline.tests;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(value = {SpringExtension.class})
public abstract class AbstractUnitTest {
}
