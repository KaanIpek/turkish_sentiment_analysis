import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.metrics import f1_score
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from py4j.java_gateway import JavaGateway
from nltk.sentiment import vader
import nltk
from sklearn.svm import SVC

gateway = JavaGateway()

nltk.download("vader_lexicon")

# Load the SWNetTR dataset
SWNetTR = pd.read_csv("try6.csv", sep=";", decimal=",", encoding="utf-8-sig")
dataset =pd.read_excel('3k-dataset (1).xlsx')

labels = []
for row in dataset['Sentiment Polarite']:

    if row == 'pozitif':
        labels.append(1)
    else:
        labels.append(-1)

# Create a dictionary of word sentiment scores
word_sentiment_dict = {}
for index, row in SWNetTR.iterrows():
    try:
        word_sentiment_dict[row[0]] = float(row[1])
    except:
        continue

# Normalize the sentiment scores
def normalize_sentiment_scores(sentiment_scores):
    sentiment_scores = {k: float(v) for k, v in sentiment_scores.items()}
    min_score = min(sentiment_scores.values())
    max_score = max(sentiment_scores.values())
    normalized_scores = {k: 2 * (v - min_score) / (max_score - min_score) - 1 for k, v in sentiment_scores.items()}
    return normalized_scores

word_sentiment_dict_normalized = normalize_sentiment_scores(word_sentiment_dict)

# Create a CountVectorizer object for text preprocessing
vectorizer = CountVectorizer()
model = SVC()
# Train a SVC model using the CountVectorizer object and the sentiment labels from SWNetTR
def train_SVC_model(data, labels):


    X_train, X_test, y_train, y_test = train_test_split(data, labels, test_size=0.2, random_state=42)
   

    model.fit(X_train, y_train)
    accuracy = model.score(X_test, y_test)
    y_pred = model.predict(X_test)
    f1 = f1_score(y_test, y_pred, average='weighted')
    print(f"Accuracy: {accuracy}")
    print(f"F1:: {f1}")
    return model
dataset = dataset.fillna('negatif')
X = dataset['Tweet']
y = labels
SVC_model = train_SVC_model(X, y)

# Normalize the text
def normalize(Article):
    result = gateway.entry_point.normalize(Article)
    return tokenize(result)

def tokenize(Article):
    result = gateway.entry_point.tokenize(Article)
    return stemming_and_lemmatization(result)

# Perform stemming and lemmatization
def stemming_and_lemmatization(Article):
    new_article = Article.split("\n")
    str = ""
    for word in new_article:
        result = word
        result = gateway.entry_point.stemming_and_lemmatization(word)

        if "Punc" not in result:
            str = str + result + " "

    return str

# Perform sentiment analysis using SVC
def get_sentiment_SVC(text):


    X = vectorizer.transform([text])
    sentiment_score = SVC_model.predict(X)
    print(sentiment_score)
    return sentiment_score[0]

# Perform sentiment analysis using SVC and a sentiment dictionary
def get_sentiment_dictnmodel(text):
    words = text.split()
    sentiment_score = 0

    # Iterate through each word in the text
    for word in words:
        # Check if the word is in the vocabulary
        if word in word_sentiment_dict_normalized:
            # If the word is in the vocabulary, get its sentiment score
            word_sentiment_score = word_sentiment_dict_normalized[word]
            # Add the sentiment score of the word to the overall sentiment score
            sentiment_score += word_sentiment_score

    # Use the SVC model to predict the sentiment
    X = vectorizer.transform([text])
    sentiment_pred = model.predict(X)[0]

    # Adjust the sentiment score based on the prediction
    if sentiment_pred == 1:
        sentiment_score += 1
    elif sentiment_pred == -1:
        sentiment_score -= 1

    # Return the final sentiment score
    return sentiment_score
text = "van’da polis ekipleri hafif ticari kamyonete narkotik köpeği ’alfa’ ile birlikte düzenledikleri operasyonda 17 kilo 400 gram eroin ele geçirildi. van’da polis ekipleri hafif ticari kamyonete narkotik köpeği ’alfa’ ile birlikte düzenledikleri operasyonda 17 kilo 400 gram eroin ele geçirildi. uyuşturucu tacirleri, narkotik köpekleri kokuyu almaması için eroinlerin yerleştirildiği yere kolonya ve parfüm döktüler. van emniyet müdürlüğü narkotik suçlarla mücadele şube müdürlüğü ekipleri, uyuşturucu tacirlerine yönelik çalışmalarını aralıksız sürdürüyor. bu çalışmalar kapsamında van üzerinden batı illerine uyuşturucu sevkiyatı yapılacağı bilgisini alan narkotik polisi, uyuşturucuyu van merkeze götürecek olana hafif ticari kamyoneti takibe aldı. kamyonet van-başkale karayolu üzerinde bulun bir tesiste beklediğinin tespit edilmesi üzerine düzenlenen operasyonla araç yakalandı. narkotik dedektör köpeği ’alfa’nın da yardımı hafif ticari kamyonette yapılan aramada tabanına zulalanmış 34 paket halinde 17 kilo 400 gram eroin maddesi ele geçirildi. olayla ilgili 2 şüpheli gözaltına alınırken, tahkikatın başlatıldığı belirtildi. uyuşturucu tacirleri, narkotik köpekleri kokuyu almaması için eroinlerin yerleştirildiği yere kolonya ve parfüm dökmeleri, narkotik köpeği hassas burun ’alfa’ya takıldı 2018 yılı içerisinde yapılan 26 operasyonda toplam 811 kilo 903 gram eroin maddesi ele geçirildi.      anasayfaya dönmek için tıklayınız  ilginç görüntüler... gram altın için böyle yarıştılar 'öğretmenim' deyip 10 yaşındaki çocuğu istismar etti! acılı anneden yürek yakan sözler: çocuğumun ağzı halen anne sütü kokuyor bursa'daki saldırının faili teröristler yakalandı!"
stemmed = normalize(text)
sentiment_SVC = get_sentiment_SVC(stemmed)
sentiment_SVC_dict = get_sentiment_dictnmodel(stemmed)
print(f"SVC model sentiment score: {sentiment_SVC}")
print(f"SVC model with sentiment dictionary sentiment score: {sentiment_SVC_dict}")