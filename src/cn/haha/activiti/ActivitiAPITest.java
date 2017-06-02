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
 * 使用ActivitiAPI进行流程操作
 * 
 * */

public class ActivitiAPITest {
	
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/*
	 * 部署流程定义
	 * 方式一：读取单个的流程定义文件
	 * 方式二：读取zip压缩文件
	 * */
	
	@Test
	public void testDeployment(){
		DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
		
		//方式一：读取单个流程文件
		/*
		deploymentBuilder.addClasspathResource("test1.bpmn");
		deploymentBuilder.addClasspathResource("test1.png");
		Deployment deployment = deploymentBuilder.deploy();
		*/
		
		//方式二：读取zip文件 ZipInputStream
		ZipInputStream zipInputStream = new ZipInputStream(this.getClass()
				.getClassLoader().getResourceAsStream("process.zip"));
		
		deploymentBuilder.addZipInputStream(zipInputStream);
		Deployment deployment = deploymentBuilder.deploy();
	} 
	/*
	 * 删除部署信息
	 * */
	/*
	@Test
	public void testDeleteDeployment(){
		String deploymentId = "1";
		//processEngine.getRepositoryService().deleteDeployment(deploymentId);
		//级联删除
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
	}
	*/
	
	/*
	 * 删除流程定义(通过删除部署信息达到删除流程定义的目的)
	 * */
	@Test
	public void testDeleteDeployment(){
		String deploymentId = "1";
		//processEngine.getRepositoryService().deleteDeployment(deploymentId);
		//级联删除
		processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
	}
	
	
	/*
	 * 查询部署列表
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
	 * 查询流程定义列表
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
	 * 查询一次部署对应的流程文件和对应的输入流（bpmn png）
	 * */
	
	@Test
	public void testDeploymentResource() throws Exception{
		String deploymentId = "5001";
		List<String> names = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
		for(String name:names){
			System.out.println(name);
			InputStream in = processEngine.getRepositoryService()
					.getResourceAsStream(deploymentId, name);
			//将文件保存到本地磁盘
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
	 * 获取png文件的输入流
	 * */
	@Test
	public void testProcessDefinitionId() throws Exception{
		String id = "qjlc:3:15004";
		InputStream is = processEngine.getRepositoryService()
				.getProcessDiagram(id);
		FileUtils.copyInputStreamToFile(is, new File("D:\\my.png"));
	}
	
	/*
	 * 启动流程实例
	 * 方式一：根据流程定义的id启动
	 * 方式二：根据流程定义的key启动（自动选择最新版本的流程定义启动流程实例）
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
	 *	查询流程实例列表  ==查询act_ru_execution
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
	 * 结束流程实例,操作表act_ru_execution，act_ru_task
	 * */
	@Test
	public void testProcessDelete(){
		String id = "7501";
		processEngine.getRuntimeService().deleteProcessInstance(id, "同意删除");
	}
	/*
	 * 查询任务列表
	 * */
	
	@Test
	public void testTaskList(){
		//任务查询对象，查询act_ru_task
		TaskQuery taskquery = processEngine.getTaskService().createTaskQuery();
		String assignee = "张三";
		taskquery.taskAssignee(assignee);
		taskquery.orderByTaskCreateTime().desc();
		List<Task> list = taskquery.list();
		for(Task task:list){
			System.out.println(task.getId()+"---"+task.getName());
		}
	
	}
	
	
	/*
	 * 办理任务
	 * */
	@Test
	public void testTaskComplete(){
		String id = "27504";
		processEngine.getTaskService().complete(id);
	}
	
	/*
	 * 直接将流程向下执行一步
	 * */
	@Test
	public void testTaskSignal(){
		String id = "30001";	//流程实例id - executionId
		processEngine.getRuntimeService().signal(id );
	}
	
	/*
	 * 查询最新版本的流程定义列表
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
