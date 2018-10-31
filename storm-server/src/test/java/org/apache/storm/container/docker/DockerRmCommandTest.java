/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.container.docker;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DockerRmCommandTest {
    private DockerRmCommand dockerRmCommand;
    private static final String CONTAINER_NAME = "container_name";

    @Before
    public void setup() {
        dockerRmCommand = new DockerRmCommand(CONTAINER_NAME);
    }

    @Test
    public void getCommandOption() {
        assertEquals("rm", dockerRmCommand.getCommandOption());
    }

    @Test
    public void getCommandWithArguments() {
        dockerRmCommand.withForce();
        assertEquals("rm --force container_name",
            dockerRmCommand.getCommandWithArguments());
    }
}