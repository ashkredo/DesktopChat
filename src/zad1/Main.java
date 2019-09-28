/**
 *
 *  @author Shkred Artur S15444
 *
 */

package zad1;

import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		try {
			new Thread(new Server()).start();

			String name1 = JOptionPane.showInputDialog("Enter username:");
			if (!(name1.isEmpty()) && !(name1.contains(" ")) && correct(name1)) {
				Thread thread1 = new Thread(new Client(name1));
				thread1.start();
			} else {
				throw new Exception("Incorrect username!");
			}

			String name2 = JOptionPane.showInputDialog("Enter username:");
			if (!(name2.isEmpty()) && !(name2.contains(" ")) && correct(name2)) {
				Thread thread2 = new Thread(new Client(name2));
				thread2.start();
			} else {
				throw new Exception("Incorrect username!");
			}
			
			String name3 = JOptionPane.showInputDialog("Enter username:");
			if (!(name3.isEmpty()) && !(name3.contains(" ")) && correct(name3)) {
				Thread thread3 = new Thread(new Client(name3));
				thread3.start();
			} else {
				throw new Exception("Incorrect username!");
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex);
			ex.printStackTrace();
		}
	}

	private static boolean correct(String username) {
		if (username.matches("\\b[a-zA-Z][a-zA-Z0-9\\._]{2,}\\b")) {
			return true;
		} else {
			return false;
		}
	}
}