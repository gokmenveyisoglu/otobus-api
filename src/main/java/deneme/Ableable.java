package deneme;


import java.io.IOException;
import java.util.concurrent.Callable;


public class Ableable implements AutoCloseable ,Runnable, Callable<islemyap4>,Cloneable,Appendable{

    public void selam(){
        System.out.println("Selam");
    }

    @Override
    public void close() throws Exception {
        System.out.println("calisti");
    }

    @Override
    public void run() {

    }


    @Override
    public islemyap4 call() throws Exception {
        return new islemyap4();
    }

    @Override
    public Ableable clone() {
        try {
            return (Ableable) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        return null;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        return null;
    }

    @Override
    public Appendable append(char c) throws IOException {
        return null;
    }
}
