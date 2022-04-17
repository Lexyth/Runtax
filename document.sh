echo "Starting Documentation"
javadoc -sourcepath ./src:target/dependency/* runtax -d docs
echo "Documentation completed"