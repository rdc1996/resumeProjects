package storagemanager;

import catalog.ACatalog;
import common.Page;
import common.Table;

import java.io.*;
import java.util.ArrayList;

public class BufferManager {
    private ArrayList<Page> currentPages;
    private int bufferSize;
    private int pageSize;

    /**
     * Constructor of the Buffer Manager with the given pageSize and bufferSize
     *
     * @param pageSize the size of each Page
     * @param bufferSize the amount of Pages the buffer can hold
     */
    public BufferManager(int pageSize, int bufferSize) {
        this.currentPages = new ArrayList<>();
        this.bufferSize = bufferSize;
        this.pageSize = pageSize;
    }

    /**
     * Checks to see if the buffer is full
     * @return true if the buffer is full, false otherwise
     */
    public boolean checkIfFull() {
        return currentPages.size() == bufferSize;
    }

    /**
     * Checks if the buffer is full. If the buffer is full, it removes the first element in the Page buffer and
     * writes the Page to hardware.
     */
    public void takeLMU() {
        if (checkIfFull()) {
            Page page = currentPages.remove(0);
            writePage(page);
        }
    }

    /**
     * Makes a Page with the given Page id and adds the Page to the Page buffer
     *
     * @param pageID the Page id of the Page
     * @return the Page with the given Page id
     */
    public Page makePage(int pageID) {
        takeLMU();
        Page page = new Page(pageID, pageSize);
        currentPages.add(page);
        return page;
    }

    /**
     * Writes the given Page to hardware. It obtains the Page id in order to obtain the path for the Page.
     * If the file already exists, it gets deleted for an updated Page file. It then converts the Page file to a
     * byte array before writing it to hardware.
     *
     * @param page the Page to write to hardware
     * @return true if successfully written, false otherwise
     */
    public boolean writePage(Page page) {
        int pageID = page.getId();
        String pagePath = ACatalog.getCatalog().getDbLocation() + "/pages/" + pageID;
        File pageFile = new File(pagePath);
        if (pageFile.exists()) {
            pageFile.delete();
        }
        try {
            if(!pageFile.createNewFile()){
                System.err.println("Error creating the page file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(pagePath);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.write(page.toBytes());
            fos.close();
            dos.close();
            return true;
        } catch (Exception ioe) {
            System.err.println("Error writing the Page to hardware.");
        }
        return false;
    }

    /**
     * If the Page is already in the Page buffer, then it finds the Page and returns that. Otherwise,
     * it obtains the path of the Page and reads in a Page file from hardware and returns the Page object
     * containing the information that was read.
     *
     * @param table the Table the Page is associated with
     * @param pageID the Page id of the Page to be read
     * @return the Page with the given Page id and information
     */
    public Page readPage(Table table, int pageID) {
        for (int pages = 0; pages < currentPages.size(); pages++) {
            if (currentPages.get(pages).getId() == pageID) {
                Page pageThatWasRead = currentPages.remove(pages);
                currentPages.add(pageThatWasRead);
                return pageThatWasRead;
            }
        }

        takeLMU();
        Page page = null;
        String pagePath = ACatalog.getCatalog().getDbLocation() + "/pages/" + pageID;

        try {
            FileInputStream fis = new FileInputStream(pagePath);
            DataInputStream dis = new DataInputStream(fis);

            page = Page.fromBytes(table, dis.readAllBytes());

            fis.close();
            dis.close();
        } catch (Exception ioe) {
            System.err.println("Error in reading from hardware and converting it to a Page object.");
        }

        currentPages.add(page);
        return page;
    }

    /**
     * Purges the buffer by writing every Page on the Page buffer to hardware if there were changes made.
     */
    public void purgeBuffer() {
        for(Page page : this.currentPages) {
            if(!page.getChanged())
                continue;

            this.writePage(page);
        }

        this.currentPages.clear();
    }

    /**
     * Removes all Pages from a Table and from hardware. It loops through the list of Pages currently in the buffer
     * and removes the Page ids if it exists in the buffer. It then creates a path for each Page and deletes the Page
     * file.
     *
     * @param pageIds a list of Page ids associated with a Table.
     * @return true if the Page was removed, false otherwise
     */
    public boolean removePages(ArrayList<Integer> pageIds) {
        for (Page p : currentPages) {
            for (int id : pageIds) {
                if (p.getId() == id) {
                    currentPages.remove(p);
                    pageIds.remove(id);
                    break;
                }
            }
        }
        for (int id : pageIds) {
            String pagePath = ACatalog.getCatalog().getDbLocation() + "/pages/" + id;
            File currPage = new File(pagePath);
            boolean result = currPage.delete();
            if (!result) {
                break;
            }
            pageIds.remove(id);
        }
        return pageIds.size() <= 0;
    }
}
