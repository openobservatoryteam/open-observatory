CERTS_DIR=src/main/resources/certs
mkdir -p $CERTS_DIR
openssl genrsa -out $CERTS_DIR/keypair.pem 4096
openssl rsa -in $CERTS_DIR/keypair.pem -pubout -out $CERTS_DIR/public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in $CERTS_DIR/keypair.pem -out $CERTS_DIR/private.pem
