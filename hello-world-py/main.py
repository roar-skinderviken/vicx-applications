import signal
import time
import logging
import sys

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(message)s')
logger = logging.getLogger(__name__)


def on_startup():
    logger.info("Hello World")


def on_sigterm(signum, frame):
    logger.info("Goodbye (sigterm). Signal: %d, frame: %s", signum, frame)
    time.sleep(30)


def on_sigint(signum, frame):
    logger.info("Goodbye (sigint). Signal: %d, frame: %s", signum, frame)
    time.sleep(30)


if __name__ == "__main__":
    on_startup()

    signal.signal(signal.SIGINT, on_sigint)
    signal.signal(signal.SIGTERM, on_sigterm)

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        logger.debug("Exiting due to keyboard interrupt.")
        sys.exit(0)
