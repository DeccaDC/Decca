package neu.lab.conflict;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.util.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.vo.DepJar;
import neu.lab.evoshell.ShellConfig;

public abstract class ConflictMojo extends AbstractMojo {
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	public MavenSession session;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	public MavenProject project;

	@Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
	public List<MavenProject> reactorProjects;

	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
	public List<ArtifactRepository> remoteRepositories;

	@Parameter(defaultValue = "${localRepository}", readonly = true)
	public ArtifactRepository localRepository;

	@Component
	public DependencyTreeBuilder dependencyTreeBuilder;

	@Parameter(defaultValue = "${project.build.directory}", required = true)
	public File buildDir;

	@Component
	public ArtifactFactory factory;

	@Component
	public ArtifactHandlerManager artifactHandlerManager;
	@Component
	public ArtifactResolver resolver;
	DependencyNode root;

	@Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
	public List<String> compileSourceRoots;

	@Parameter(property = "ignoreTestScope", defaultValue = "false")
	public boolean ignoreTestScope;

	@Parameter(property = "ignoreProvidedScope", defaultValue = "false")
	public boolean ignoreProvidedScope;

	@Parameter(property = "ignoreRuntimeScope", defaultValue = "false")
	public boolean ignoreRuntimeScope;

	@Parameter(property = "append", defaultValue = "false")
	public boolean append;

	@Parameter(property = "useAllJar", defaultValue = "true")
	public boolean useAllJar;

	@Parameter(property = "disDepth")
	public int disDepth = Integer.MAX_VALUE;

	@Parameter(property = "pathDepth")
	public int pathDepth = Integer.MAX_VALUE;

	@Parameter(property = "callConflict")
	public String callConflict = null;

	//自定义输出目录
	@Parameter(property = "resultPath")
	public String resultPath = null;
	
	//设置是否细分1234等级
	@Parameter(property = "subdivisionLevel", defaultValue = "false")
	public boolean subdivisionLevel;
	
	@Parameter(property = "findAllPath")
	public boolean findAllPath = false;

	public int systemSize = 0;

	public long systemFileSize = 0;//byte

	//初始化全局变量
	protected void initGlobalVar() throws Exception {

		MavenUtil.i().setMojo(this);

		Conf.DOG_DEP_FOR_DIS = disDepth;
		Conf.DOG_DEP_FOR_PATH = pathDepth;
		Conf.callConflict = callConflict;
		Conf.findAllpath = findAllPath;
		UserConf.setOutDir(resultPath);
		GlobalVar.useAllJar = useAllJar;
		ShellConfig.mvnRep = MavenUtil.i().getMvnRep();

		//初始化NodeAdapters
		NodeAdapters.init(root);
		//初始化DepJars
		DepJars.init(NodeAdapters.i());// occur jar in tree

		validateSysSize();

		//初始化所有的类集合
		AllCls.init(DepJars.i());
		
		Conflicts.init(NodeAdapters.i());// version conflict in tree	初始化树中的版本冲突
	}

	private void validateSysSize() throws Exception {

		for (DepJar depJar : DepJars.i().getAllDepJar()) {
			if (depJar.isSelected()) {
				systemSize++;
				for (String filePath : depJar.getJarFilePaths(true)) {
					systemFileSize = systemFileSize + new File(filePath).length();
				}
			}
		}

		MavenUtil.i().getLog().warn("tree size:" + DepJars.i().getAllDepJar().size() + ", used size:" + systemSize
				+ ", usedFile size:" + systemFileSize / 1000);

		//		if (DepJars.i().getAllDepJar().size() <= 50||systemFileSize / 1000>20000) {
		//			throw new Exception("project size error.");
		//		}
	}

	@Override
	public void execute() throws MojoExecutionException {
		this.getLog().info("method detect start:");
		long startTime = System.currentTimeMillis();
		String pckType = project.getPackaging();	//得到项目的打包类型
		if ("jar".equals(pckType) || "war".equals(pckType) || "maven-plugin".equals(pckType)
				|| "bundle".equals(pckType)) {
			try {
				// project.
				root = dependencyTreeBuilder.buildDependencyTree(project, localRepository, null);
			} catch (DependencyTreeBuilderException e) {
				throw new MojoExecutionException(e.getMessage());
			}
			try {
				initGlobalVar();
			} catch (Exception e) {
				MavenUtil.i().getLog().error(e);
				throw new MojoExecutionException("project size error!");
			}
			run();

		} else {
			this.getLog()
					.info("this project fail because package type is neither jar nor war:" + project.getGroupId() + ":"
							+ project.getArtifactId() + ":" + project.getVersion() + "@"
							+ project.getFile().getAbsolutePath());
		}
		long runtime = (System.currentTimeMillis() - startTime) / 1000;
		GlobalVar.runTime = runtime;
		printRunTime();
		this.getLog().debug("method detect end");

	}

	private void printRunTime() {
		this.getLog().info("time to run:" + GlobalVar.runTime);
		this.getLog().info("time to call graph:" + GlobalVar.time2cg);
		this.getLog().info("time to run dog:" + GlobalVar.time2runDog);
		this.getLog().info("time to calculate branch:" + GlobalVar.branchTime);
		this.getLog().info("time to calculate reference:" + GlobalVar.time2calRef);
		this.getLog().info("time to filter riskMethod:" + GlobalVar.time2filterRiskMthd);
	}

	public abstract void run();
}
