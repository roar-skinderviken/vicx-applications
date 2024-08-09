import time
import atexit
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(message)s')
logger = logging.getLogger(__name__)


def on_startup():
    logger.info("Hello World")


def on_shutdown():
    logger.info("Good Bye")

    # emulate long running shutdown
    time.sleep(30)


if __name__ == "__main__":
    on_startup()

    # Register shutdown hook
    atexit.register(on_shutdown)

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        logger.debug("Exiting due to keyboard interrupt.")
