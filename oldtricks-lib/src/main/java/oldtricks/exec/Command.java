package oldtricks.exec;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import oldtricks.util.StringUtil;

public class Command {
	private String[] cmdline;
	private Map<String, String> env;
	private File workDir;
	private boolean inheritIO;

	public String[] getCmdline() {
		return cmdline;
	}

	public void setCmdline(String[] cmdline) {
		this.cmdline = cmdline;
	}

	public Map<String, String> getEnv() {
		return env;
	}

	public void setEnv(Map<String, String> env) {
		this.env = env;
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public boolean isInheritIO() {
		return inheritIO;
	}

	public void setInheritIO(boolean inheritIO) {
		this.inheritIO = inheritIO;
	}

	@Override
	public String toString() {
		return "Command [cmdline=" + StringUtil.join(cmdline, " ") + ", env=" + env + ", workDir=" + workDir
				+ ", inheritIO=" + inheritIO + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(cmdline);
		result = prime * result + ((env == null) ? 0 : env.hashCode());
		result = prime * result + (inheritIO ? 1231 : 1237);
		result = prime * result + ((workDir == null) ? 0 : workDir.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		if (!Arrays.equals(cmdline, other.cmdline))
			return false;
		if (env == null) {
			if (other.env != null)
				return false;
		} else if (!env.equals(other.env))
			return false;
		if (inheritIO != other.inheritIO)
			return false;
		if (workDir == null) {
			if (other.workDir != null)
				return false;
		} else if (!workDir.equals(other.workDir))
			return false;
		return true;
	}

}
