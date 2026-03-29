package ge.tbc.testautomation.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Map<String, Integer> retryCountByMethod = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        if (!method.isAnnotationPresent(RetryCount.class)) {
            return false;
        }
        RetryCount annotation = method.getAnnotation(RetryCount.class);
        Object[] params = result.getParameters();
        String key = result.getTestClass().getName() + "#" + method.getName()
                + Arrays.hashCode(params != null ? params : new Object[0])
                + "_" + Thread.currentThread().getId();
        int count = retryCountByMethod.getOrDefault(key, 0);
        if (count < annotation.count()) {
            retryCountByMethod.put(key, count + 1);
            return true;
        }
        retryCountByMethod.remove(key);
        return false;
    }
}
