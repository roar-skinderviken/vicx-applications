# cd html-pages
# docker build -t my-static-site .
# docker run --name my-static-site -p 80:80 my-static-site:latest
# docker rm my-static-site # remove stopped container
# docker rmi my-static-site # remove Docker-image

FROM nginx:alpine

COPY content/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80