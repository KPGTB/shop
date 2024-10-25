package eu.kpgtb.shop.serivce.iface;

import java.io.File;

public interface IS3Service {
    void uploadFile(File file, String key, String bucket);
    void uploadFile(byte[] bytes, String key, String bucket);
    void uploadFile(String url, String key, String bucket);
    byte[] getFile(String key, String bucket);
}
