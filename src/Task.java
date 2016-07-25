class Task {

	private String type;
	private String args;

	Task(String type, String args) {
		this.type = type;
		this.args = args;
	}
	
	String type() {
		return type;
	}

	String args() {
		return args;
	}
}