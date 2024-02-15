from locust import HttpUser, task
import random

class Consumer(HttpUser):

    @task
    def batch_create_datapoint(self):
        self.client.get(f"/api/dummy?count={count}", name="/api/dummy")
