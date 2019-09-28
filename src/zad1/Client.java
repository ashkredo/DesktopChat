/**
 *
 *  @author Shkred Artur S15444
 *
 */

package zad1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
//import java.net.InetAddress;
//import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Client extends JFrame implements Runnable {
  private String newName;
  private String lastMessage = "";
  private Font font1 = new Font(null, Font.CENTER_BASELINE, 30);
  private Font font2 = new Font(null, Font.BOLD, 24);
  private Font font3 = new Font(null, Font.PLAIN, 15);
  private JButton jButton;
  private JTextField jTextField;
  private JTextArea jTextArea;
  private StringBuffer stringBufferForReadAndWrite;
  private ByteBuffer byteBufferForRead, byteBufferForWrite;
  private int allocate = 1024;
  private String returnedMessages = "";
  private JPanel jPanel1, jPanel2;
  private GridBagConstraints gridBagConstraints1, gridBagConstraints2;
  private JScrollPane jScrollPane;
  private InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 7777);
  private SocketChannel socketChannelServer;
  private SelectionKey selectedKey;
  private SocketChannel socketChannel;
  private Server server;

  public Client(String name) {
    super("CZATNIO_Shkred_Artur_s15444");
    newName = name;
    jPanel1 = new JPanel();
    gridBagConstraints1 = new GridBagConstraints();
    gridBagConstraints2 = new GridBagConstraints();
    jPanel1.setLayout(new BorderLayout());
    jTextField = new JTextField(30);
    jTextField.requestFocusInWindow();
    jTextField.setFont(font1);
    jTextField.setBackground(Color.lightGray);
    jTextField.setForeground(Color.white);
    jTextField.setSelectedTextColor(Color.gray);
    jButton = new JButton("SEND");
    jButton.setFont(font2);
    jTextArea = new JTextArea();
    jTextArea.setEditable(false);
    jTextArea.setFont(font3);
    jTextArea.setLineWrap(true);
    jTextArea.setBackground(Color.gray);
    jTextArea.setSelectedTextColor(Color.blue);
    jTextArea.setForeground(Color.black);
    jScrollPane = new JScrollPane(jTextArea);
    jPanel1.add(jScrollPane, BorderLayout.CENTER);
    gridBagConstraints1.anchor = GridBagConstraints.LINE_START;
    gridBagConstraints2.anchor = GridBagConstraints.LINE_END;
    gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints2.fill = GridBagConstraints.NONE;
    gridBagConstraints1.weightx = 512D;
    gridBagConstraints2.weightx = 1D;
    gridBagConstraints1.weighty = 1D;
    gridBagConstraints2.weighty = 1D;
    jPanel2 = new JPanel();
    jPanel2.setLayout(new GridBagLayout());
    jPanel2.add(jTextField, gridBagConstraints1);
    jPanel2.add(jButton, gridBagConstraints2);
    jPanel1.add(BorderLayout.SOUTH, jPanel2);
    add(jPanel1);
    setContentPane(jPanel1);
    SwingUtilities.getRootPane(jButton).setDefaultButton(jButton);
    jTextField.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent fe) {
        jTextField.setBackground(Color.DARK_GRAY);
      }

      @Override
      public void focusLost(FocusEvent fe) {
        jTextField.setBackground(Color.lightGray);
      }
    });
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        System.out.println(getName() + "- ONLINE");
      }

      @Override
      public void windowClosing(WindowEvent e) {
        // Object[] options = { "Yes", "No!" };
        // int o = JOptionPane.showOptionDialog(e.getWindow(), "This client
        // window will be closed, close the others too?",
        // Are you sure?", JOptionPane.YES_NO_OPTION,
        // JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        System.out.println(newName + "- OFFLINE");
        stringBufferForReadAndWrite = new StringBuffer();
        stringBufferForReadAndWrite.append("<" + newName + ">: ").append("- OFFLINE");
        server.setOnlineHistory("<" + name + ">: - OFFLINE");
        byteBufferForWrite = ByteBuffer.allocate(allocate);
        byteBufferForWrite.put(stringBufferForReadAndWrite.toString().getBytes(Charset.forName("UTF8")));
        byteBufferForWrite.flip();
        try {
          socketChannel.write(byteBufferForWrite);
        } catch (Exception ex) {
        }
        server.zapis("<" + newName + ">: " + "- OFFLINE");
        returnedMessages += "<" + newName + ">: " + "- OFFLINE\n";
        // if (o == 0) {
        // System.out.println("All clients- OFFLINE by " + newName);
        // e.getWindow().setVisible(false);
        // System.exit(0);
        // }
      }
    });
    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (1 > jTextField.getText().length()) {
          return;
        } else if (jTextField.getText().equals("/clear")) {
          jTextArea.setText(new Date().toString() + '\n' + "Cleared all messages! " + '\n');
          jTextField.setText("");
          return;
        } else if (jTextField.getText().equals("/returnMessages")) {
          jTextArea.setText(new Date().toString() + '\n' + "Returned all messages! " + '\n' + returnedMessages);
          jTextField.setText("");
          return;
        } else if (jTextField.getText().equals("/lastMessage")) {
          jTextField.setText(lastMessage);
          return;
        } else if (jTextField.getText().equals("/commands")) {
          jTextArea.setText("Commands:\n" + "/clear - Cleared all messages!\n"
              + "/returnMessages - Returned all messages!\n" + "/lastMessage - Writed the last message!\n"
              + "/online - Writed online clients name!\n" + "/admins - Writed the admins name!\n");
          jTextField.setText("");
          return;
        } else if (jTextField.getText().equals("/online")) {
          jTextArea.setText(server.getOnlineHistory());
          jTextField.setText("");
          return;
        } else if (jTextField.getText().equals("/admins")) {
          jTextArea.setText("Admin: " + server.adminName);
          jTextField.setText("");
          return;
        } else if (jTextField.getText().equals("/admin")) {
          String password = JOptionPane.showInputDialog("Enter password:");
          if (password.equals(server.adminPassword)) {
            server.adminCommandHistory += ("Admin menu was opened!" + " - " + new Date() + '\n');
            jTextArea.setText("Welcome! Developer: " + server.adminName + '\n' + "Commands:\n"
                + "/q - Closed admin menu!\n"
                + "/allMessages - Writed the history of the messages from \"History.txt\"!\n"
                + "/deleteMessages - Deleted the history of the messages from \"History.txt\"!\n"
                + "/online - Writed online clients name!\n" + "/newPassword - Changed the password from admin menu!\n"
                + "/exit - Closed the server connection!\n" + "/rename - Changed your name in chat!\n"
                + "/adminCommandHistory - Writed the admin commands history!\n");
            jTextField.setText("");
            String adminCommand = "";
            while (!(adminCommand.equals("/q"))) {
              adminCommand = JOptionPane.showInputDialog("Enter the admin command:");
              if (adminCommand.equals("/allMessages")) {
                server.adminCommandHistory += ("/allMessages" + " - " + new Date() + '\n');
                jTextArea.setText("Developer: " + server.adminName + "\n" + Server.history);
              } else if (adminCommand.equals("/deleteMessages")) {
                server.adminCommandHistory += ("/deleteMessages" + " - " + new Date() + '\n');
                server.deleteAllMessageHistory();
                jTextArea.setText("Developer: " + server.adminName + "\nDeleted all messages!\n");
              } else if (adminCommand.equals("/online")) {
                server.adminCommandHistory += ("/online" + " - " + new Date() + '\n');
                jTextArea.setText("Developer: " + server.adminName + "\n" + server.getOnlineHistory());
              } else if (adminCommand.equals("/newPassword")) {
                server.adminCommandHistory += ("/newPassword" + " - " + new Date() + '\n');
                server.adminPassword = JOptionPane.showInputDialog("Enter new password:");
                jTextArea.setText("Developer: " + server.adminName + "\n" + "Password was changed!");
              } else if (adminCommand.equals("/exit")) {
                server.adminCommandHistory += ("/exit" + " - " + new Date() + '\n');
                System.out.println("admin " + server.adminName + " closed the server connection!");
                System.exit(0);
              } else if (adminCommand.equals("/commands")) {
                jTextArea.setText(server.adminName + "\nCommands:\n" + "/q - Closed admin menu!\n"
                    + "/allMessages - Writed the history of the messages from \"History.txt\"!\n"
                    + "/deleteMessages - Deleted the history of the messages from \"History.txt\"!\n"
                    + "/online - Writed online clients name!\n"
                    + "/newPassword - Changed the password from admin menu!\n"
                    + "/exit - Closed the server connection!\n" + "/rename - Changed your name in chat!\n"
                    + "/adminCommandHistory - Writed the admin commands history!\n");
                jTextField.setText("");
                return;
              } else if (adminCommand.equals("/rename")) {
                newName = JOptionPane.showInputDialog(null, "Enter new username:");
                jTextArea.setText("Developer: " + server.adminName + "\n" + name + " changed name to " + newName + "!");
              } else if (adminCommand.equals("/adminCommandHistory")) {
                jTextArea.setText(
                    "Developer: " + server.adminName + "\nAdmin commands history:\n" + server.adminCommandHistory);
              }
            }
            jTextArea.setText("Admin menu was closed!\n");
            server.adminCommandHistory += ("Admin menu was closed!" + " - " + new Date() + '\n');
          } else {
            JOptionPane.showMessageDialog(null,
                "Wrong password: " + password + '\n' + "Please check that you have entered your password correctly!");
          }
          jTextField.setText("");
          return;
        }
        try {
          stringBufferForReadAndWrite = new StringBuffer();
          stringBufferForReadAndWrite.append("<" + newName + ">: ").append(jTextField.getText());
          byteBufferForWrite = ByteBuffer.allocate(allocate);
          byteBufferForWrite.put(stringBufferForReadAndWrite.toString().getBytes(Charset.forName("UTF8")));
          byteBufferForWrite.flip();
          server.zapis("<" + newName + ">: " + jTextField.getText());
          lastMessage = jTextField.getText();
          returnedMessages += "<" + newName + ">: " + jTextField.getText() + '\n';
          socketChannel.write(byteBufferForWrite);
          jTextField.setText("");
          // Thread.sleep(250); //--wait before sending next message
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex);
        }
      }
    });
    setSize(500, 500);
    setVisible(true);
    setResizable(false);
    setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    try {
      String newClientName = JOptionPane.showInputDialog("Enter username:");
      if (!(newClientName.isEmpty()) && !(newClientName.contains(" "))
          && newClientName.matches("\\b[a-zA-Z][a-zA-Z0-9\\._]{2,}\\b")) {
        (new Thread(new Client(newClientName))).start();
      } else {
        throw new Exception("Incorrect username!");
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex);
      ex.printStackTrace();
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return newName;
  }

  @Override
  public void run() {
    try {
      socketChannelServer = SocketChannel.open();
      Selector channelsSelector = Selector.open();
      socketChannelServer.configureBlocking(!true);
      socketChannelServer.connect(inetSocketAddress);
      socketChannelServer.register(channelsSelector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
      System.out.println(newName + "- " + "connects to Server on port 7777...");
      while (true) {
        Set<SelectionKey> setSelectorKeys;
        Iterator<SelectionKey> iteratorSelectorKeys;
        if (0 == channelsSelector.select()) {
          continue;
        }
        channelsSelector.select();
        setSelectorKeys = channelsSelector.selectedKeys();
        iteratorSelectorKeys = setSelectorKeys.iterator();
        while (iteratorSelectorKeys.hasNext()) {
          selectedKey = (SelectionKey) iteratorSelectorKeys.next();
          if (selectedKey.isReadable()) {
            byteBufferForRead = ByteBuffer.allocate(allocate);
            stringBufferForReadAndWrite = new StringBuffer();
            while (0 < ((SocketChannel) selectedKey.channel()).read(byteBufferForRead)) {
              byteBufferForRead.rewind();
              stringBufferForReadAndWrite.append(Charset.forName("UTF8").decode(byteBufferForRead) + "");
              byteBufferForRead.flip();
            }
            System.out.println("REad " + stringBufferForReadAndWrite.length());
            jTextArea.append(stringBufferForReadAndWrite.toString() + '\n');
          } else if (selectedKey.isConnectable()) {
            socketChannel = (SocketChannel) selectedKey.channel();
            socketChannel.finishConnect();
            selectedKey.interestOps(SelectionKey.OP_READ);
            stringBufferForReadAndWrite = new StringBuffer();
            stringBufferForReadAndWrite.append("<" + newName + ">: ").append("- ONLINE");
            server.zapis("<" + newName + ">: " + "- ONLINE");
            returnedMessages += "<" + newName + ">: " + "- ONLINE\n";
            server.setOnlineHistory("<" + newName + ">: - ONLINE");
            byteBufferForWrite = ByteBuffer.allocate(allocate);
            byteBufferForWrite.put(stringBufferForReadAndWrite.toString().getBytes(Charset.forName("UTF8")));
            byteBufferForWrite.flip();
            socketChannel.write(byteBufferForWrite);
          }
          iteratorSelectorKeys.remove();
        }
        setSelectorKeys.clear();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex);
    }
  }
}