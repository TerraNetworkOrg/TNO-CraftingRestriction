package terranetworkorg.CraftingRestriction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CraftingRestrictionUtility {
    public static CraftingRestriction plugin;

    public CraftingRestrictionUtility(CraftingRestriction instance) {
        plugin = instance;
    }

    public void copy(InputStream inputThis, File sFile) throws IOException{
        InputStream in = inputThis;
        OutputStream out = new FileOutputStream(sFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }
}
