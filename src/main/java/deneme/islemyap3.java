package deneme;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;

public class islemyap3 implements Callable {

    @Override
    public Object call() throws Exception {
        return "Mahmut";
    }
}
