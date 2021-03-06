/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.neo4j;

import org.activiti.neo4j.behavior.BehaviorMapping;
import org.activiti.neo4j.behavior.BehaviorMappingImpl;
import org.activiti.neo4j.manager.ExecutionManager;
import org.activiti.neo4j.manager.NodeBaseExecutionManager;
import org.activiti.neo4j.manager.NodeBasedTaskManager;
import org.activiti.neo4j.manager.TaskManager;
import org.neo4j.graphdb.GraphDatabaseService;


/**
 * @author Joram Barrez
 */
public class ProcessEngineConfiguration {
  
  protected GraphDatabaseService graphDatabaseService;
  protected BehaviorMapping behaviorMapping;
  protected Core core;
  protected CommandExecutor commandExecutor;
  protected ExecutionManager executionManager;
  protected TaskManager taskManager;
  
  public ProcessEngine buildProcessEngine() {
    ProcessEngine processEngine = new ProcessEngine();
    processEngine.setGraphDatabaseService(graphDatabaseService);
    
    initBehaviorMapping();
    initCore();
    initManagers();
    initCommandExecutor(processEngine);
    initServices(processEngine);
    
    return processEngine;
  }
  
  protected void initBehaviorMapping() {
    this.behaviorMapping = new BehaviorMappingImpl();
  }
  
  protected void initCore() {
    CoreImpl core = new CoreImpl();
    core.setBehaviorMapping(behaviorMapping);
    this.core = core;
  }
  
  protected void initManagers() {
    NodeBaseExecutionManager nodeBaseExecutionManager = new NodeBaseExecutionManager();
    nodeBaseExecutionManager.setGraphDb(graphDatabaseService);
    this.executionManager = nodeBaseExecutionManager;
    
    NodeBasedTaskManager nodeBasedTaskManager = new NodeBasedTaskManager();
    nodeBasedTaskManager.setGraphDb(graphDatabaseService);
    this.taskManager = nodeBasedTaskManager;
  }

  protected void initCommandExecutor(ProcessEngine processEngine) {
    CommandExecutor commandExecutor = new CommandExecutor(graphDatabaseService);
    commandExecutor.setCore(core);
    commandExecutor.setExecutionManager(executionManager);
    
    processEngine.setCommandExecutor(commandExecutor);
    this.commandExecutor = commandExecutor;
  }
  
  protected void initServices(ProcessEngine processEngine) {
    RepositoryService repositoryService = new RepositoryService(graphDatabaseService, commandExecutor);
    processEngine.setRepositoryService(repositoryService);
    
    RuntimeService runtimeService = new RuntimeService(graphDatabaseService, commandExecutor);
    processEngine.setRuntimeService(runtimeService);
    
    TaskService taskService = new TaskService(commandExecutor);
    taskService.setTaskManager(taskManager);
    processEngine.setTaskService(taskService);
  }
  
  public GraphDatabaseService getGraphDatabaseService() {
    return graphDatabaseService;
  }

  public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = graphDatabaseService;
  }
  
}
