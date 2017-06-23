package queue;

import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

/**
 * Created by xiaolin  on 2017/3/31.
 */
public class DistributedSimpleQueueListener implements IZkChildListener {
    public void handleChildChange(String s, List<String> list) throws Exception {

    }
}
