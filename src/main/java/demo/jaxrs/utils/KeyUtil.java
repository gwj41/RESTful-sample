package demo.jaxrs.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Key;
/**
 * @Name:
 * @Author: lizhao（作者）
 * @Version: V1.00 （版本号）
 * @Create Date: 2015-11-26（创建日期）
 * @Description:
 */
public class KeyUtil {
    private static Log logger = LogFactory.getLog(KeyUtil.class);
    public static Key getKey() {
        String filePath = Thread.currentThread().getContextClassLoader().getResource("key/key.txt").getFile();
        File file = new File(filePath);
        try {
            if(file.length() == 0){
                Key key = MacProvider.generateKey(SignatureAlgorithm.HS512);
                ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(file));
                oo.writeObject(key);
                oo.close();
                return key;
            }
            ObjectInputStream ois = null;
            ois = new ObjectInputStream(new FileInputStream(file));
            Key key = (Key) ois.readObject();
            return key;
        } catch (Exception e) {
            logger.debug(e);
            e.printStackTrace();
            return null;
        }
    }
//    @Test
//    public  void test() throws Exception {
//        getKey();
//    }
}
