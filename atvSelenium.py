from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from selenium.common.exceptions import TimeoutException

print("Site 1")
driver = webdriver.Chrome()
driver.get("https://www.saucedemo.com/")

wait = WebDriverWait(driver, 10)
driver.find_element(By.ID, "user-name").send_keys("standard_user")
driver.find_element(By.ID, "password").send_keys("secret_sauce")
driver.find_element(By.ID, "login-button").click()

assert wait.until(EC.presence_of_element_located((By.ID, "inventory_container")))
print("Login realizado com sucesso!")
driver.quit()
print("Site 2")
driver = webdriver.Chrome()
driver.get("https://the-internet.herokuapp.com/login")

wait = WebDriverWait(driver, 10)
driver.find_element(By.ID, "username").send_keys("tomsmith")
driver.find_element(By.ID, "password").send_keys("SuperSecretPassword!")
driver.find_element(By.XPATH, "//*[@class='fa fa-2x fa-sign-in']").click()
time.sleep(3)
try:
    WebDriverWait(driver, 5).until(EC.alert_is_present())
    alerta = driver.switch_to.alert
    alerta.accept()
    print("Alerta de 'mudar senha' foi encontrado e aceito.")
except TimeoutException:
    print("Nenhum alerta encontrado, continuando a execução.")
assert wait.until(EC.presence_of_element_located((By.XPATH, "//*[@class='button secondary radius']")))

print("Login realizado com sucesso!")
driver.quit()

print("Site 3")
driver = webdriver.Chrome()
driver.get("https://practicetestautomation.com/practice-test-login/")

wait = WebDriverWait(driver, 10)
driver.find_element(By.ID, "username").send_keys("student")
driver.find_element(By.ID, "password").send_keys("Password123")
driver.find_element(By.ID, "submit").click()
logout_button = driver.find_element(By.LINK_TEXT, "Log out")
logout_button.click()
print("Login realizado com sucesso!")
driver.quit()

print("Site 4")
driver = webdriver.Chrome()
driver.get("https://opensource-demo.orangehrmlive.com/")

wait = WebDriverWait(driver, 10)
username_field = wait.until(EC.visibility_of_element_located((By.NAME, "username")))
username_field.send_keys("Admin")

password_field = wait.until(EC.visibility_of_element_located((By.NAME, "password")))
password_field.send_keys("admin123")

login_button = wait.until(EC.element_to_be_clickable((By.XPATH, "//*[@class='oxd-button oxd-button--medium oxd-button--main orangehrm-login-button']")))
login_button.click()

user_dropdown = wait.until(EC.element_to_be_clickable((By.XPATH, "//*[@class='oxd-userdropdown-name']")))
user_dropdown.click()

logout_link = wait.until(EC.element_to_be_clickable((By.LINK_TEXT, "Logout")))
logout_link.click()
print("Login realizado com sucesso!")
driver.quit()
