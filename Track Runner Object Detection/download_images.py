from sklearn import kernel_approximation
from simple_image_download import simple_image_download as simp

response = simp.simple_image_download

keywords = ["track and field runners"]

for keyword in keywords:
    response().download(keyword, 200)