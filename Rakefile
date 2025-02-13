
desc '清理旧文件'
task :clean do
  sh "rm -rf gen-java gen-py"
end

desc '生成代码'
task :gen => :clean do
  sh "thrift -gen java template/pojo.thrift"
  sh "thrift -gen java template/service.thrift"
  sh "thrift -gen py template/service.thrift"
end

desc '打包'
task :package => :gen do
  sh "mvn -T 1.5C  clean compile assembly:single -Dmaven.test.skip=true"
end

