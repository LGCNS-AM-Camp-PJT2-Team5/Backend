FROM mysql:8.0

# 환경 변수 설정 (GitHub Secrets에서 전달됨)
ARG MYSQL_ROOT_PASSWORD
ARG MYSQL_DATABASE
ARG MYSQL_USER
ARG MYSQL_PASSWORD

# 환경 변수를 MySQL 설정에 적용
ENV MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
ENV MYSQL_DATABASE=${MYSQL_DATABASE}
ENV MYSQL_USER=${MYSQL_USER}
ENV MYSQL_PASSWORD=${MYSQL_PASSWORD}

# 초기화 SQL 복사
COPY ./jobbotdari_init.sql /docker-entrypoint-initdb.d/
COPY ./jobbotdari_user_init.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
