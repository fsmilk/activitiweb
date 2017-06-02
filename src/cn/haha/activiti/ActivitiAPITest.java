package cn.haha.activiti;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/*
 * 
 * ʹ��ActivitiAPI�������̲���
 * 
 * */

public class ActivitiAPITest {
	
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/*
	 * �������̶���
	 * ��ʽһ����ȡ���������̶����ļ�
	 * ��ʽ������ȡzipѹ���ļ�
	 * */
	
	@Test
	public void testDeployment(){
		DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
		
		//��ʽһ����ȡ���������ļ�
		/*
		deploymentBuilder.addClasspathResource("test1.bpmn");
		deploymentBuilder.addClasspathResource("test1.png");
		Deployment deployment = deploymentBuilder.deploy();
		*/
		
		//��ʽ������ȡzip�ļ� ZipInputStream
		ZipInputStream zipInputStream = new ZipInputStream(this.getClass()
				.getClassLoader().getResourceAsStream("process.zip"));
		
		deploymentBuilder.addZipInputStream(zipInputStream);
		Deployment deployment = deploymentBuilder.deploy();
	} 
	/*
	 * ɾ��������Ϣ
	 * */
	/*
	@Test
	public void testDeleteDeployment(){
		String deploymentId = "1";
		//processEngine.getRepositoryService().deleteDeployment(deploymentId);
		//����ɾ��
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
	}
	*/
	
	/*
	 * ɾ�����̶���(ͨ��ɾ��������Ϣ�ﵽɾ�����̶����Ŀ��)
	 * */
	@Test
	public void testDeleteDeployment(){
		String deploymentId = "1";
		//processEngine.getRepositoryService().deleteDeployment(deploymentId);
		//����ɾ��
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
	}
	
	
	/*
	 * ��ѯ�����б�
	 * */
	@Test
	public void testDeploymentlist(){
		DeploymentQuery deploymentQuery  = processEngine.getRepositoryService().createDeploymentQuery();
		
		List<Deployment> list = deploymentQuery.list();
		
		for(Deployment deployment:list){
			
			System.out.println(deployment.getId());
		}
		
		
	}
	
	/*
	 * ��ѯ���̶����б�
	 * */
	@Test
	public void testProcessDefinitionQuery(){
		ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
		
		List<ProcessDefinition> list = query.list();
		
		for(ProcessDefinition pd : list){
			System.out.println(pd.getName());
		}
	}
	
	
	/*
	 * ��ѯһ�β����Ӧ�������ļ��Ͷ�Ӧ����������bpmn png��
	 * */
	
	@Test
	public void testDeploymentResource() throws Exception{
		String deploymentId = "5001";
		List<String> names = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
		for(String name:names){
			System.out.println(name);
			InputStream in = processEngine.getRepositoryService()
					.getResourceAsStream(deploymentId, name);
			//���ļ����浽���ش���
			/*
			OutputStream os = new FileOutputStream(new File("d:\\" + name));
			
			byte[] b = new byte[1024];
			int len = 0;
			while((len = in.read(b))!= -1){
				os.write(b,0,len);				
			}
			os.close();
			*/
			
			FileUtils.copyInputStreamToFile(in, new File("d:\\" + name));
			in.close();
		}
	}
	
	/*
	 * ��ȡpng�ļ���������
	 * */
	@Test
	public void testProcessDefinitionId() throws Exception{
		String id = "qjlc:3:15004";
		InputStream is = processEngine.getRepositoryService()
				.getProcessDiagram(id);
		FileUtils.copyInputStreamToFile(is, new File("D:\\my.png"));
	}
	
	/*
	 * ��������ʵ��
	 * ��ʽһ���������̶����id����
	 * ��ʽ�����������̶����key�������Զ�ѡ�����°汾�����̶�����������ʵ����
	 * */
	@Test
	public void testProcessDefinition(){
		/*
		String id = "qjlc:3:15004";
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(id);
		System.out.println(processInstance.getId());
		*/
		
		String processDefintionKey = "qjlc";
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(processDefintionKey);
		System.out.println(processInstance.getId());
		
	}
	
	/*
	 *	��ѯ����ʵ���б�  ==��ѯact_ru_execution
	 * */
	@Test
	public void testProcessInstanceQuery(){
		ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
		List<ProcessInstance> list = query.list();
		query.processDefinitionKey("qjlc");
		query.orderByProcessDefinitionId().desc();
		query.listPage(0, 2);
		for(ProcessInstance pi :list){
			System.out.println(pi.getId() + "---" + pi.getActivityId());
		}
	}
	
	/*
	 * ��������ʵ��,������act_ru_execution��act_ru_task
	 * */
	@Test
	public void testProcessDelete(){
		String id = "7501";
		processEngine.getRuntimeService().deleteProcessInstance(id, "ͬ��ɾ��");
	}
	/*
	 * ��ѯ�����б�
	 * */
	
	@Test
	public void testTaskList(){
		//�����ѯ���󣬲�ѯact_ru_task
		TaskQuery taskquery = processEngine.getTaskService().createTaskQuery();
		String assignee = "����";
		taskquery.taskAssignee(assignee);
		taskquery.orderByTaskCreateTime().desc();
		List<Task> list = taskquery.list();
		for(Task task:list){
			System.out.println(task.getId()+"---"+task.getName());
		}
	
	}
	
	
	/*
	 * ��������
	 * */
	@Test
	public void testTaskComplete(){
		String id = "27504";
		processEngine.getTaskService().complete(id);
	}
	
	/*
	 * ֱ�ӽ���������ִ��һ��
	 * */
	@Test
	public void testTaskSignal(){
		String id = "30001";	//����ʵ��id - executionId
		processEngine.getRuntimeService().signal(id );
	}
	
	/*
	 * ��ѯ���°汾�����̶����б�
	 * */
	
	@Test
	public void testNewProcessList(){
		ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
		query.orderByProcessDefinitionVersion().asc();
		List<ProcessDefinition> list = query.list();
		Map<String,ProcessDefinition> map = new HashMap<String,ProcessDefinition>();
		for(ProcessDefinition pd:list){
			map.put(pd.getKey(), pd);
		}
		//System.out.println(map);
		
		ArrayList<ProcessDefinition> lastList = new ArrayList<>(map.values());
		for(ProcessDefinition pd:lastList){
			System.out.println(pd.getName() + "---" + pd.getVersion());
		}
		
		
	}
}
