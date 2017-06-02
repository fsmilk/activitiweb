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

	// ʹ��Ĭ�����ý���
	@Test
	public void testProcessEngineConfiguration() {
		processEngine = ProcessEngines.getDefaultProcessEngine();
	}

	@Test
	//�������̶��壨act_re_deployment/act_re_procdef/act_ge_bytearray��
	public void testDeployment(){
		//���һ�����̹������������ڼ������̶����ļ���test1.bpmn,test1.png����ɶ������̲���
		DeploymentBuilder db = processEngine.getRepositoryService().createDeployment();
		//�������̶����ļ�
		db.addClasspathResource("test1.bpmn");
		db.addClasspathResource("test1.png");
		//�������̶���
		Deployment deployment = db.deploy();
		System.out.println(deployment.getId());
	}
	
	/*��ѯ���̶����б�*/
	@Test
	public void testProcessDefinitionQuery(){
		//���̶����ѯ���󣬲�ѯact_re_procdef
		ProcessDefinitionQuery query = processEngine.getRepositoryService()
				.createProcessDefinitionQuery();
		//��ӹ�������
		query.processDefinitionKey("qjlc");
		//�����������
		query.orderByProcessDefinitionVersion().desc();
		//��ӷ�ҳ��ѯ
		query.listPage(0, 10);
		
		List<ProcessDefinition> list = query.list();
		for(ProcessDefinition pd:list){
			System.out.println(pd.getId());
		}
	}
	
	/*
	 * �������̶����ID����һ������ʵ��
	 * */
	@Test
	public void testProcessInstance(){
		String id = "qjlc:2:5004";
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(id);
		System.out.println(processInstance.getId());
	}
	/*
	 * ��ѯ���������б�
	 * */
	@Test
	public void testTaskQuery(){
		TaskQuery query = processEngine.getTaskService().createTaskQuery();
		String assignee = "����";
		query.taskAssignee(assignee);
		List<Task> list = query.list();
		for(Task task:list){
			System.out.println(task.getId() + "   " + task.getName());
		}
	}
	
	/*
	 * ��������
	 * */
	@Test
	public void testComplete(){
		String taskId = "10002";
		processEngine.getTaskService().complete(taskId);
	}

}
