package org.yetiz.performance.burn;

import java.util.ArrayList;

/**
 * Created by yeti on 15/9/6.
 */
public interface Sender {
	void start(ArrayList<Req> reqList, int loopTimes);
}
