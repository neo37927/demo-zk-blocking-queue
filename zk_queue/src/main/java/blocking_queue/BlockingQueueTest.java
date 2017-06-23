package blocking_queue;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import queue.User;

/**
 * Created by xiaolin  on 2017/3/31.
 */
public class BlockingQueueTest {
    public static void main(String[] args) {
        try {
            ZkClient zkClient = new ZkClient("10.3.1.6:2181", 5000, 5000, new SerializableSerializer());

            BlockingQueue blockingQueue = new BlockingQueue<User>(zkClient,"/log");

            User user1 = new User();
            user1.setId("1");
            user1.setName("呜呜呜呜呜呜");

            User user2 = new User();
            user2.setId("2");
            user2.setName("哈哈哈哈哈");

           /* String elementId1 = blockingQueue.add(user1);
            String elementId2 = blockingQueue.add(user2);
            System.out.println(elementId1);
            System.out.println(elementId2);*/

            BlockingQueue.Element<User> result = blockingQueue.getFirstElement();
            System.out.println(blockingQueue.);
            System.out.println(blockingQueue.size());
            System.out.println(result.getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
