package eu.kpgtb.shop.serivce.iface;

public interface ICaptchaService {
    boolean verify(String token);
}
