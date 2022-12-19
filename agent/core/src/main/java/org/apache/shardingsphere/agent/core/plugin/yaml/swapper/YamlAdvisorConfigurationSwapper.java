/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.agent.core.plugin.yaml.swapper;

import net.bytebuddy.matcher.ElementMatchers;
import org.apache.shardingsphere.agent.config.advisor.AdvisorConfiguration;
import org.apache.shardingsphere.agent.config.advisor.MethodAdvisorConfiguration;
import org.apache.shardingsphere.agent.core.plugin.advisor.AdvisorConfigurationRegistryFactory;
import org.apache.shardingsphere.agent.core.plugin.yaml.entity.YamlAdvisorConfiguration;
import org.apache.shardingsphere.agent.core.plugin.yaml.entity.YamlPointcutConfiguration;

/**
 * YAML advisor configuration swapper.
 */
public final class YamlAdvisorConfigurationSwapper {
    
    /**
     * Swap from YAML advisor configuration to advisors configuration.
     * 
     * @param yamlAdvisorConfig YAML advisor configuration
     * @param type type
     * @return advisor configuration
     */
    public AdvisorConfiguration swapToObject(final YamlAdvisorConfiguration yamlAdvisorConfig, final String type) {
        AdvisorConfiguration result = AdvisorConfigurationRegistryFactory.getRegistry(type).getAdvisorConfiguration(yamlAdvisorConfig.getTarget());
        String[] constructPointcuts = yamlAdvisorConfig.getPointcuts().stream().filter(each -> "construct".equals(each.getType())).map(YamlPointcutConfiguration::getName).toArray(String[]::new);
        if (constructPointcuts.length > 0) {
            result.getAdvisors().add(new MethodAdvisorConfiguration(ElementMatchers.isConstructor(), yamlAdvisorConfig.getAdvice()));
        }
        String[] methodPointcuts = yamlAdvisorConfig.getPointcuts().stream().filter(each -> "method".equals(each.getType())).map(YamlPointcutConfiguration::getName).toArray(String[]::new);
        if (methodPointcuts.length > 0) {
            result.getAdvisors().add(new MethodAdvisorConfiguration(ElementMatchers.namedOneOf(methodPointcuts), yamlAdvisorConfig.getAdvice()));
        }
        return result;
    }
}
