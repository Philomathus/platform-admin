FROM public.ecr.aws/r2e4u8r8/openjdk-8-jre:latest

RUN mkdir -p /opt/platform-admin/logs/ /opt/platform-admin/tmp/

COPY target/platform-admin.jar /opt/platform-admin/app.jar

WORKDIR /opt/platform-admin

CMD ["java", \
	"-Djava.io.tmpdir=/opt/platform-admin/tmp/", \
	"-Djava.library.path=/usr/local/lib/", \
	"-Djava.security.egd=file:/dev/./urandom", \
	"-Dfile.encoding=UTF-8", \
	"-jar", "/opt/platform-admin/app.jar" \
]
