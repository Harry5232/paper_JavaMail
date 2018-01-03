import javax.mail.*;
import javax.mail.internet.*;

import java.util.*;
import java.io.*;

//收信：建立學生ID/課程/作業
public class App4 {
	public static void main(String[] args) {
		Properties props = new Properties();
		String host = "smtp.gmail.com";
		String username = "wayne19911126";
		String password = "morepowermoreduty";
		String provider = "imaps";
		File filePath = new File(System.getProperty("user.dir"));
		try {
			// Connect to the POP3 server
			Session session = Session.getInstance(props);
			Store store = session.getStore(provider);
			store.connect(host, username, password);
			// Open the folder
			Folder inbox = store.getFolder("INBOX");
			if (inbox == null) {
				System.out.println("No INBOX");
				System.exit(1);
			}
			inbox.open(Folder.READ_ONLY);
			// Get the messages from the server
			Message[] messages = inbox.getMessages();
			for (int i = 0; i < messages.length; i++) { // messages.length
				System.out.println("------------ Message " + (i + 1)
						+ " ------------");

				// // Print message headers
				// @SuppressWarnings("unchecked")
				// Enumeration<Header> headers = messages[i].getAllHeaders();
				// while (headers.hasMoreElements()) {
				// Header h = headers.nextElement();
				// System.out.println(h.getName() + ": " + h.getValue());
				// }

				String from = InternetAddress.toString(messages[i].getFrom());
				System.out.println("From: " + from);

				String to = InternetAddress.toString(messages[i]
						.getRecipients(Message.RecipientType.TO));
				System.out.println("To: " + to);

				Date received = messages[i].getReceivedDate();
				System.out.println("Date: " + received);

				String subject = messages[i].getSubject();
				System.out.println("Subject: " + subject);

				String subjectSub[] = subject.split("_");
				if (subjectSub.length >= 3) {
					String stID = subjectSub[0];
					String course = subjectSub[1];
					String hw = subjectSub[2];

					String path = stID +"\\"+course +"\\"+hw;
					filePath = new File(path);
					if (!filePath.exists()) {
						filePath.mkdirs();
					}
				}

				String name = messages[i].getFileName();
				System.out.println("File Name: " + name);

				System.out.println();
				// Enumerate parts
				Object body = messages[i].getContent();
				if (body instanceof Multipart) {
					processMultipart((Multipart) body,filePath);
				} else { // ordinary message
					processPart(messages[i],filePath);
				}
				System.out.println();
			}
			// Close the connection
			// but don't remove the messages from the server
			inbox.close(false);
		} catch (MessagingException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void processMultipart(Multipart mp,File dir) throws MessagingException {
		for (int i = 0; i < mp.getCount(); i++) {
			processPart(mp.getBodyPart(i),dir);
		}
	}

	public static void processPart(Part p,File dir) {
		try {
			String fileName = p.getFileName();
			String disposition = p.getDisposition();
			String contentType = p.getContentType();
			if (contentType.toLowerCase().startsWith("multipart/")) {
				processMultipart((Multipart) p.getContent(),dir);
			} else if (fileName == null
					&& (Part.ATTACHMENT.equalsIgnoreCase(disposition) || !contentType
							.equalsIgnoreCase("text/plain"))) {
				// pick a random file name.
//				File file_google= new File(dir, "attachment.txt");
				fileName = File.createTempFile("attachment", ".txt").getName();
			}
			if (fileName == null) { // likely inline
				p.writeTo(System.out);
			} else {
				File f = new File(dir,fileName);
				// find a file that does not yet exist
				for (int i = 1; f.exists(); i++) {
					String newName = fileName + " " + i;
					f = new File(dir,newName);
				}
				try (OutputStream out = new BufferedOutputStream(
						new FileOutputStream(f));
						InputStream in = new BufferedInputStream(
								p.getInputStream())) {
					// We can't just use p.writeTo() here because it doesn't
					// decode the attachment. Instead we copy the input stream
					// onto the output stream which does automatically decode
					// Base-64, quoted printable, and a variety of other
					// formats.
					int b;
					while ((b = in.read()) != -1)
						out.write(b);
					out.flush();
				}
			}
		} catch (IOException | MessagingException ex) {
			ex.printStackTrace();
		}
	}
}