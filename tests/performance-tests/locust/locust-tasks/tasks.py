import random

from locust import FastHttpUser, TaskSet, task, between

class MetricsTaskSet(TaskSet):
    wait_time = between(1, 5)

    def on_start(self):
        print("Executed on start")

    @task(1)
    def call_health_check(self):
        self.client.get('/cirene/health')

    @task(5)
    def call_planet_loading_size(self):
        random_size = round(random.uniform(10.05, 500.10), 1)
        radius_param = f'radius={random_size}'
        self.client.get(f'/cirene/planet/size/info?{radius_param}')    

class MetricsLocust(FastHttpUser):
    tasks = {MetricsTaskSet}
