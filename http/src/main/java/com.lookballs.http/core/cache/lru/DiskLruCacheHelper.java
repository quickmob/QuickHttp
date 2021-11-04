package com.lookballs.http.core.cache.lru;

import com.lookballs.http.core.utils.QuickUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * 缓存工具(参考自https://github.com/hongyangAndroid/base-diskcache)
 */
public class DiskLruCacheHelper {
    private static final String TAG = "DiskLruCacheHelper";

    private static final int MAX_COUNT = 10 * 1024 * 1024;
    private static final int DEFAULT_APP_VERSION = 1;
    private static final int DEFAULT_VALUE_COUNT = 1;

    private DiskLruCache mDiskLruCache;

    public DiskLruCacheHelper(File dir) throws IOException {
        this(dir, MAX_COUNT);
    }

    public DiskLruCacheHelper(File dir, long maxCount) throws IOException {
        mDiskLruCache = DiskLruCache.open(
                dir,
                DEFAULT_APP_VERSION,
                DEFAULT_VALUE_COUNT,
                maxCount);
    }

    public boolean put(String key, String... values) {
        DiskLruCache.Editor editor = null;
        BufferedWriter bw = null;
        try {
            editor = editor(key);
            if (editor == null) {
                return false;
            }
            OutputStream os = editor.newOutputStream(0);
            bw = new BufferedWriter(new OutputStreamWriter(os));
            for (String value : values) {
                bw.write(value);
            }
            editor.commit();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getAsString(String key) {
        String str = null;
        InputStream is = null;
        try {
            is = getInputStream(key);
            str = Util.readFully(new InputStreamReader(is, Util.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public boolean put(String key, Serializable value) {
        DiskLruCache.Editor editor = null;
        ObjectOutputStream oos = null;
        try {
            editor = editor(key);
            if (editor == null) {
                return false;
            }
            OutputStream os = editor.newOutputStream(0);
            oos = new ObjectOutputStream(os);
            oos.writeObject(value);
            oos.flush();
            editor.commit();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public <T> T getAsSerializable(String key) {
        T t = null;
        InputStream is = null;
        ObjectInputStream ois = null;
        try {
            is = getInputStream(key);
            ois = new ObjectInputStream(is);
            t = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public boolean remove(String key) throws IOException {
        return mDiskLruCache.remove(QuickUtils.md5(key));
    }

    public void close() throws IOException {
        mDiskLruCache.close();
    }

    public void delete() throws IOException {
        mDiskLruCache.delete();
    }

    public void flush() throws IOException {
        mDiskLruCache.flush();
    }

    public boolean isClosed() {
        return mDiskLruCache.isClosed();
    }

    public long size() {
        return mDiskLruCache.size();
    }

    public File getDirectory() {
        return mDiskLruCache.getDirectory();
    }

    public long getMaxSize() {
        return mDiskLruCache.getMaxSize();
    }

    public DiskLruCache.Editor editor(String key) throws IOException {
        DiskLruCache.Editor edit = mDiskLruCache.edit(QuickUtils.md5(key));
        return edit;
    }

    public InputStream getInputStream(String key) throws IOException {
        return getSnapshot(key).getInputStream(0);
    }

    public DiskLruCache.Snapshot getSnapshot(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(QuickUtils.md5(key));
        return snapshot;
    }
}



