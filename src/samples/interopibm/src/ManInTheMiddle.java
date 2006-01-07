
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Created on Dec 21, 2005
 *
 */

/**
 * @author Dasarath Weeratunge
 *  
 */
public class ManInTheMiddle {

	public static interface TestOrchestrator {

		boolean onPrepareMsg(String msg);

		boolean onPreparedMsg(String msg);

		boolean onCommitMsg(String msg);

		boolean onCommittedMsg(String msg);

		boolean onRollbackMsg(String msg);

		boolean onAbortedMsg(String msg);

		boolean onReplayMsg(String msg);

	}

	public static class TestOrchestratorImpl implements TestOrchestrator {

		protected int stage = 0;

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onPrepareMsg(java.lang.String)
		 */
		public synchronized boolean onPrepareMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onPreparedMsg(java.lang.String)
		 */
		public synchronized boolean onPreparedMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onCommitMsg(java.lang.String)
		 */
		public synchronized boolean onCommitMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onCommittedMsg(java.lang.String)
		 */
		public synchronized boolean onCommittedMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onRollbackMsg(java.lang.String)
		 */
		public synchronized boolean onRollbackMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onAbortedMsg(java.lang.String)
		 */
		public synchronized boolean onAbortedMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ManInTheMiddle.TestOrchestrator#onReplayMsg(java.lang.String)
		 */
		public synchronized boolean onReplayMsg(String msg) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	public static class RetryPreparedCommitTestOrchestratorImpl extends
			TestOrchestratorImpl {

		public synchronized boolean onPrepareMsg(String msg) {
			if (stage == 0) {
				stage = 5;
				return false;
			}
			return true;
		}

		public synchronized boolean onPreparedMsg(String msg) {
			if (stage == 5) {
				stage = 6;
				return true;
			} else
				return stage < 6;
		}
	}

	public static class RetryPreparedAbortTestOrchestratorImpl extends
			RetryPreparedCommitTestOrchestratorImpl {

		public synchronized boolean onPreparedMsg(String msg) {
			if (stage == 6)
				stage = 8;
			return stage < 6;
		}

		public synchronized boolean onRollbackMsg(String msg) {
			if (stage == 5) {
				stage = 6;
				return true;
			}
			return !(stage > 7);
		}
	}

	public static class PreparedAfterTimeoutTestOrchestratorImpl extends
			TestOrchestratorImpl {

		public synchronized boolean onPrepareMsg(String msg) {
			if (stage == 0) {
				stage = 6;
				return false;
			} else if (stage == 7) {
				stage = 8;
				return false;
			}
			return true;
		}

		public synchronized boolean onPreparedMsg(String msg) {
			if (stage == 6) {
				stage = 7;
				return false;
			} else if (stage == 11) {
				stage = 12;
				return false;
			} else if (stage == 12) {
				stage = 13;
				return false;
			} else
				return true;
		}

		public synchronized boolean onRollbackMsg(String msg) {
			if (stage == 8) {
				stage = 10;
				return true;
			} else if (stage == 10) {
				stage = 11;
				return true;
			}
			return stage < 13;
		}
	}

	public static class RetryCommitTestOrchestratorImpl extends
			TestOrchestratorImpl {

		public synchronized boolean onPreparedMsg(String msg) {
			if (stage == 0) {
				stage = 6;
				return false;
			}
			return stage >= 6;
		}

		public synchronized boolean onCommitMsg(String msg) {
			if (stage == 6) {
				stage = 7;
				return true;
			}
			return false;
		}
	}

	public static class LostCommittedTestOrchestratorImpl extends
			TestOrchestratorImpl {

		public synchronized boolean onCommittedMsg(String msg) {
			return true;
		}

	}

	public static class ReplayAbortTestOrchestratorImpl extends
			RetryPreparedCommitTestOrchestratorImpl {

		public synchronized boolean onPreparedMsg(String msg) {
			return true;
		}

	}

	public static class ReplayCommitTestOrchestratorImpl extends
			TestOrchestratorImpl {

		public synchronized boolean onCommittedMsg(String msg) {
			return stage != 5;
		}

		public synchronized boolean onReplayMsg(String msg) {
			stage = 5;
			return false;
		}
	}

	/**
	 * @param testOrchestrator
	 */
	public ManInTheMiddle(TestOrchestrator testOrchestrator) {
		this.testOrchestrator = testOrchestrator;
	}

	private TestOrchestrator testOrchestrator;

	private boolean dropMsg(String msg) {
		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepare\"") != -1)
			return testOrchestrator.onPrepareMsg(msg);

		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepared") != -1)
			return testOrchestrator.onPreparedMsg(msg);

		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Aborted") != -1)
			return testOrchestrator.onAbortedMsg(msg);

		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Rollback") != -1)
			return testOrchestrator.onRollbackMsg(msg);

		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Commit\"") != -1)
			return testOrchestrator.onCommitMsg(msg);

		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Committed") != -1)
			return testOrchestrator.onCommittedMsg(msg);

		if (msg.indexOf("http://schemas.xmlsoap.org/ws/2004/10/wsat/Replay") != -1)
			return testOrchestrator.onReplayMsg(msg);

		return false;
	}

	private void forward(final int srcPort, final int destPort)
			throws IOException {

		new Thread() {
			public void run() {

				ServerSocket s;

				try {
					s = new ServerSocket(srcPort);
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

				while (true) {

					final Socket inS;

					try {
						inS = s.accept();
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}

					new Thread() {
						public void run() {
							int n;
							byte buf1[] = new byte[1024];
							ByteArrayOutputStream buf2 = new ByteArrayOutputStream();

							try {

								buf2.reset();
								InputStream is = inS.getInputStream();
								while (buf2.toString().indexOf(
									"</soapenv:Envelope>") == -1
										&& (n = is.read(buf1)) > 0) {
									buf2.write(buf1, 0, n);
								}

								if (dropMsg(buf2.toString())) {
									System.out.println();
									System.out.println(buf2.toString());

									inS.close();
									return;
								}

								Socket outS = new Socket("127.0.0.1", destPort);

								OutputStream os = outS.getOutputStream();
								os.write(buf2.toByteArray());

								is = outS.getInputStream();
								buf2.reset();
								while (buf2.toString().indexOf(
									"</soapenv:Envelope>") == -1
										&& (n = is.read(buf1)) > 0)
									buf2.write(buf1, 0, n);

								os = inS.getOutputStream();
								os.write(buf2.toByteArray());

								inS.close();
								outS.close();

							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException(e);
							}
						}
					}.start();
				}
			}
		}.start();
	}

	public static void main(String[] args) throws Exception {

		ManInTheMiddle middleMan = new ManInTheMiddle(
				new ManInTheMiddle.PreparedAfterTimeoutTestOrchestratorImpl());
		middleMan.forward(8081, 8083);
		middleMan.forward(8082, 8084);

	}

}