/*
 *  Copyright (c), WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.jaggeryjs2;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Log {

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(Log.class);
    private final Logger logger;

    public Log(String fileName){
        Logger currentLogger = Logger.getLogger(fileName);
        //TODO get LOG LEVEL app context
        String appLogLevel = "info";
        if(currentLogger.getLevel() == null){
            currentLogger.setLevel(Level.toLevel(appLogLevel));
        }
        this.logger = currentLogger;
    }

    public void info(String message){
        logger.info(message);
    }

    public void debug(String message){
        logger.debug(message);
    }

    public void error(String message){
        logger.error(message);
    }

    public void fatal(String message){
        logger.fatal(message);
    }

    public void trace(String message){
        logger.trace(message);
    }

    public void warn(String message){
        logger.warn(message);
    }

}
