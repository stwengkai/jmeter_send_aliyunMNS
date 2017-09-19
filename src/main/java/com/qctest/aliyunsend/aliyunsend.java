package com.qctest.aliyunsend;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;

public class aliyunsend extends AbstractJavaSamplerClient {
    private SampleResult results;
    private  String AccessId ;
    private  String AccessKey ;
    private  String EndPoint ;
    private  String QueueName ;
    private  String Mge ;
    private static CloudAccount account;
    private static MNSClient client;
    private static CloudQueue queue;
    private static Message message;

    private void setupValues(JavaSamplerContext context)
      {
        results = new SampleResult();
         Mge = context.getParameter("Mge","");
            if(Mge != null && Mge.length() > 0){
                results.setSamplerData(Mge);
            }
      }

      //设置传入的参数，可以设置多个，已设置的参数会显示到Jmeter的参数列表中
    public Arguments getDefaultParameters(){
        Arguments params = new Arguments();
        //定义一个参数，显示到Jmeter的参数列表中，第一个参数为参数默认的显示名称，第二个参数为默认值
        params.addArgument("AccessId","");
        params.addArgument("AccessKey","");
        params.addArgument("EndPoint","");
        params.addArgument("QueueName","");
        params.addArgument("Mge","");

        return params;
    }

    //初始化方法，实际运行时每个线程仅执行一次，在测试方法运行前执行，类似于LoadRunner中的init方法
    public void setupTest(JavaSamplerContext arg0){
        results = new SampleResult();
        AccessId = arg0.getParameter("AccessId","");
        if(AccessId != null && AccessId.length() > 0){
            results.setSamplerData(AccessId);
        }

        AccessKey = arg0.getParameter("AccessKey","");
        if(AccessKey != null && AccessKey.length() > 0){
            results.setSamplerData(AccessKey);
        }
        EndPoint = arg0.getParameter("EndPoint","");
        if(EndPoint != null && EndPoint.length() > 0){
            results.setSamplerData(EndPoint);
        }
        QueueName = arg0.getParameter("QueueName","");
        if(QueueName != null && QueueName.length() > 0){
            results.setSamplerData(QueueName);
        }
        account = new CloudAccount(AccessId, AccessKey, EndPoint);
        client = account.getMNSClient();
        queue = client.getQueueRef(QueueName);
        message = new Message();

    }

    //测试执行的循环体，根据线程数和循环次数的不同可执行多次，类似于LoadRunner中的Action方法
    public SampleResult runTest(JavaSamplerContext arg0){
        setupValues(arg0);

        results.sampleStart();

        try {

                message.setMessageBody(Mge);

                Message putMsg = queue.putMessage(message);
                results.setResponseData("Send MegId:" + putMsg.getMessageId(),null);

        } catch (Exception e) {
            results.setSuccessful(false);
            e.printStackTrace();
         }finally{
            results.setSuccessful(true);
                //标记事务结束            
            results.sampleEnd();
            }
        return results;
    }
    //结束方法，实际运行时每个线程仅执行一次，在测试方法运行结束后执行，类似于LoadRunner中的end方法
    public void tearDownTest(JavaSamplerContext arg0){
        client.close();
    }

}
