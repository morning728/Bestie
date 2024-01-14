FROM nginx
COPY ./default.conf /etc/nginx/conf.d/default.conf

# # Nginx config
# RUN rm -rf /etc/nginx/conf.d
# COPY ./default.conf /etc/nginx/conf.d/default.conf

# # Static build
# COPY --from=builder ../app/build /usr/share/nginx/html/

# # Default port exposure
# EXPOSE 80

# # Copy .env file and shell script to container
# WORKDIR /usr/share/nginx/html
# COPY ./env.sh .
# COPY .env .

# # Make our shell script executable
# RUN chmod +x env.sh

# # Start Nginx server
# CMD ["/bin/sh", "-c", "/usr/share/nginx/html/env.sh && nginx -g \"daemon off;\""]