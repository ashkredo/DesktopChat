/**
 *
 *  @author Shkred Artur S15444
 *
 */

package zad1;

import java.io.BufferedWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

public class Server implements Runnable {
  private static Path path = Paths.get(
      "src" + System.getProperty("file.separator") + "zad1" + System.getProperty("file.separator") + "History.txt");
  static String history = "History from " + new Date() + ":\n";
  private static String onlineHistory = "Online history from " + new Date() + ":\n";
  private static FileChannel fileChannel;
  private Selector channelsSelector;
  private ServerSocketChannel serverSocketChannel;
  private SocketChannel socketChannel;
  private SocketChannel socketChannelForWriteAndRead;
  static private ByteBuffer byteBufferForWriteAndRead;
  static private StringBuffer stringBufferForRead;
  static String adminName = "Shkred Artur (s15444)";
  static String adminPassword = "s15444";
  static String adminCommandHistory = "";
  private static int allocate = 1024;
  private static Charset charset = Charset.forName("UTF-8");
  static private InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 7777);
  private SelectionKey selectionKey;
  @SuppressWarnings("rawtypes")
  private Set setSelectorKeys;
  @SuppressWarnings("rawtypes")
  private Iterator iteratorSelectorKeys;

  public static void main(String[] args) {
    (new Thread(new Server())).start();
  }

  @Override
  public void run() {
    try {
      openHistoryPilk();
      serverSocketChannel = ServerSocketChannel.open();
      channelsSelector = Selector.open();
      serverSocketChannel.socket().bind(inetSocketAddress);
      serverSocketChannel.configureBlocking(!true);
      serverSocketChannel.register(channelsSelector, SelectionKey.OP_ACCEPT);
      String string;
      System.out.println("Server- waits for clients on port 7777...");
      while (true) {
        if (0 == channelsSelector.select()) {
          continue;
        }
        channelsSelector.select();
        setSelectorKeys = channelsSelector.selectedKeys();
        iteratorSelectorKeys = setSelectorKeys.iterator();
        while (iteratorSelectorKeys.hasNext()) {
          selectionKey = (SelectionKey) iteratorSelectorKeys.next();
          if (selectionKey.isReadable()) {
            socketChannelForWriteAndRead = (SocketChannel) selectionKey.channel();
            stringBufferForRead = new StringBuffer();
            byteBufferForWriteAndRead = ByteBuffer.allocate(allocate);
            while (0 < socketChannelForWriteAndRead.read(byteBufferForWriteAndRead)) {
              //byteBufferForWriteAndRead.rewind();
              byteBufferForWriteAndRead.flip();
              stringBufferForRead.append(Charset.forName("UTF8").decode(byteBufferForWriteAndRead) + "");
//              byteBufferForWriteAndRead.flip();
            }
            string = stringBufferForRead + "";
            System.out.println("string _ " + string.length());
            for (SelectionKey sKeys : channelsSelector.keys()) {
              if (sKeys.channel().equals(serverSocketChannel)) {
                continue;
              } else if (!(sKeys.channel().equals(serverSocketChannel))) {
                socketChannelForWriteAndRead = (SocketChannel) sKeys.channel();
                byteBufferForWriteAndRead = ByteBuffer.allocate(allocate);
                byteBufferForWriteAndRead.put(string.getBytes(charset));
                byteBufferForWriteAndRead.flip();
              }
              if (!(string.isEmpty()) && (socketChannelForWriteAndRead.write(byteBufferForWriteAndRead) > 0)) {
                socketChannelForWriteAndRead.write(byteBufferForWriteAndRead);
              }
            }
          } else if (selectionKey.isAcceptable()) {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(!true);
            socketChannel.register(channelsSelector, (SelectionKey.OP_WRITE | SelectionKey.OP_READ));
          }
          iteratorSelectorKeys.remove();
        }
        setSelectorKeys.clear();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex);
    }
  }

  public static void setOnlineHistory(String string) {
    onlineHistory += string + " - " + new Date() + '\n';
  }

  public static String getOnlineHistory() {
    return onlineHistory;
  }

  private static void openHistoryPilk() {
    try {
      Files.deleteIfExists(path);
      // -- Za kazdym uruchomieniu programu plik bedzie zawierać tylko aktualnie
      // przeczytane dane z plikow
      fileChannel = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND);
      // (sciezka pliku, utworzy nowy plik, cos zostanie zapisane na końcu
      // pliku)
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void zapis(String string) {
    try (BufferedWriter writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND)) {
      writer.write(string + '\n');
      history += string + '\n';
      fileChannel.close();
    } catch (Exception ex) {
    }
  }

  public static void deleteAllMessageHistory() {
    try {
      Files.delete(path);
      history = "History from " + new Date() + ":\n";
      openHistoryPilk();
    } catch (Exception ex) {
    }
  }
}