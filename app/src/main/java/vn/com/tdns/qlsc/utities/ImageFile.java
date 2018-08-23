package vn.com.tdns.qlsc.utities;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import vn.com.tdns.qlsc.R;

/**
 * Created by ThanLe on 12/8/2017.
 */

public class ImageFile {

    public static File getFile(Context context) {
        String path = Environment.getExternalStorageDirectory().getPath();
        File outFile = new File(path, context.getResources().getString(R.string.path_saveImage));
        if (!outFile.exists())
            outFile.mkdir();
        return new File(outFile, "xxx.png");
    }

}
