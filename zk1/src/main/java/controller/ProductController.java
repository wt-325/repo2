package controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import services.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static String connStr = "192.168.227.130:2181,192.168.227.132:2181";

    @Autowired
    private ProductService productService;

    /**
     * 之所以要使用分布式锁是为了保证数据一致性,即数据库里的数据要和现实生活中的数据一致
     */
    @GetMapping("/reduce")
    @ResponseBody
    public Object reduce(int id) throws Exception {
        // 重试策略,1s重试1次,最多重试3次,若超过3s还连不上集群则报错
        ExponentialBackoffRetry retryPolice = new ExponentialBackoffRetry(1000, 5);
        // 创建creator客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(connStr, retryPolice);
        client.start();
        // 根据客户端工具对象创建内部互斥锁,该锁内部会帮我们创建临时有序节点
        // 抢到锁的线程在product_1节点下创建临时有序节点
        InterProcessMutex mutex = new InterProcessMutex(client, "/product_" + id);
        try {
            // 加锁
            mutex.acquire();
            productService.reduceStock(id);
        } catch (Exception e){
           if (e instanceof RuntimeException) {
               throw e;
           }
        } finally {
            Thread.sleep(2000);
            mutex.release();
        }
        return "ok";
    }
}
