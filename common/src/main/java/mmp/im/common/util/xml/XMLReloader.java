package mmp.im.common.util.xml;

import java.io.File;

public class XMLReloader implements Runnable {

    private String fileName;

    public XMLReloader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        long lastModifiedTime;
        File file;
        try {
            file = new File(this.fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // TODO 初始化一个静态Map放DOM节点值
        XML xml = new XML(this.fileName);

        lastModifiedTime = file.lastModified();
        while (true) {
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (file.lastModified() == lastModifiedTime)
                continue;
            // TODO 更新节点值
            xml = new XML(this.fileName);

            lastModifiedTime = file.lastModified();
        }
    }
}
