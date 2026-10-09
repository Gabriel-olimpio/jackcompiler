# Execute com: source ./use-java24.sh
# Seleciona o JDK 24 nesta sessao do terminal (macOS).
jackcompiler_java_home=$(/usr/libexec/java_home -v 24) || return 1
export JAVA_HOME="$jackcompiler_java_home"
export PATH="$JAVA_HOME/bin:$PATH"
unset jackcompiler_java_home
