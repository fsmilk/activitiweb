package cn.haha.activiti;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

public class HelloWorld {

	ProcessEngine processEngine =ProcessEngines.getDefaultProcessEngine();

	public HelloWorld() {
		// TODO Auto-generated constructor stub
	}

	// 使用默认配置建表
	@Test
	public void testProcessEngineConfiguration() {
		processEngine = ProcessEngines.getDefaultProcessEngine();
	}

	@Test
	//部署流程定义（act_re_deployment/act_re_procdef/act_ge_bytearray）
	public void testDeployment(){
		//获得一个流程构建器对象，用于加载流程定义文件（test1.bpmn,test1.png）完成定义流程部署
		DeploymentBuilder db = processEngine.getRepositoryService().createDeployment();
		//加载流程定义文件
		db.addClasspathResource("test1.bpmn");
		db.addClasspathResource("test1.png");
		//部署流程定义
		Deployment deployment = db.deploy();
		System.out.println(deployment.getId());
	}
	
	/*查询流程定义列表*/
	@Test
	public void testProcessDefinitionQuery(){
		//流程定义查询对象，查询act_re_procdef
		ProcessDefinitionQuery query = processEngine.getRepositoryService()
				.createProcessDefinitionQuery();
		//添加过滤条件
		query.processDefinitionKey("qjlc");
		//添加排序条件
		query.orderByProcessDefinitionVersion().desc();
		//添加分页查询
		query.listPage(0, 10);
		
		List<ProcessDefinition> list = query.list();
		for(ProcessDefinition pd:list){
			System.out.println(pd.getId());
		}
	}
	
	/*
	 * 根据流程定义的ID启动一个流程实例
	 * */
	@Test
	public void testProcessInstance(){
		String id = "qjlc:2:5004";
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(id);
		System.out.println(processInstance.getId());
	}
	/*
	 * 查询个人任务列表
	 * */
	@Test
	public void testTaskQuery(){
		TaskQuery query = processEngine.getTaskService().createTaskQuery();
		String assignee = "张三";
		query.taskAssignee(assignee);
		List<Task> list = query.list();
		for(Task task:list){
			System.out.println(task.getId() + "   " + task.getName());
		}
	}
	
	/*
	 * 办理任务
	 * */
	@Test
	public void testComplete(){
		String taskId = "10002";
		processEngine.getTaskService().complete(taskId);
	}

}
