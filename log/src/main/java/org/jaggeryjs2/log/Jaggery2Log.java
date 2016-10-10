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

package org.jaggeryjs2.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Jaggery2Log {

    private static final Log log = LogFactory.getLog(Jaggery2Log.class);
    private Logger logger;

    public Jaggery2Log Jaggery2Log(String fileName){
        Jaggery2Log logObj = new Jaggery2Log();
        Logger currentLogger = Logger.getLogger(fileName);
        //TODO get LOG LEVEL app context
        String appLogLevel = "info";
        if(currentLogger.getLevel() == null){
            currentLogger.setLevel(Level.toLevel(appLogLevel));
        }
        logObj.logger = currentLogger;
        return logObj;
    }

    private static void info(String message){
        log.info(message);
        return;
    }

    private static void debug(String message){
        log.debug(message);
        return;
    }

    private static void error(String message){
        log.error(message);
        return;
    }

    private static void fatal(String message){
        log.fatal(message);
        return;
    }

    private static void trace(String message){
        log.trace(message);
        return;
    }

    private static void warn(String message){
        log.warn(message);
        return;
    }

}
