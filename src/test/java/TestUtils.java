import com.betarec.data.pojo.PojoParser;
import gen.data.pojo.Movie;
import com.betarec.utils.Ticker;
import gen.service.ModelService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.betarec.utils.FunctionUtils.topN;

public class TestUtils {
    Logger logger = LoggerFactory.getLogger(TestUtils.class);

    @Test
    public void testToString() {
        Movie movie = PojoParser.parseMovie("1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy");
        logger.info("movie:{}", movie);
    }

    @Test
    public void testLog() {
        logger.info("hello info log");
        logger.error("hello error log");
    }

    @Test
    public void testThrift() throws TException {
        TTransport transport = new TBinaryProtocol(new TSocket("localhost", 8848)).getTransport();
        ModelService.Client client = new ModelService.Client(new TBinaryProtocol(transport));

        // 打开传输
        transport.open();

        String resp = client.ping("ping");
        logger.info("resp:{}", resp);

        // 关闭传输
        transport.close();

    }

    @Test
    public void testTopN() {
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            lst.add(i);
        }
        List<Integer> topN = topN(lst, 10, (a,b)->{
            return Integer.compare(b,a);
        });
        logger.info("topN:{}", topN);
    }

    @Test
    public void testTicker() throws InterruptedException {
        Ticker ticker = new Ticker();
        Map<String, Integer> events = Map.of(
                "A", 10,
                "B", 20,
                "C", 30
        );
        for (Map.Entry<String, Integer> entry : events.entrySet()) {
            TimeUnit.MILLISECONDS.sleep(entry.getValue());
            ticker.tick(entry.getKey());
        }
        logger.info("ticker:{}", ticker);
    }
}
