from collections import Counter
from random import randint, sample, seed

seed(108)


def genPolygons(num):
  numOdd = randint(0, num // 2) * 2
  numEven = num - numOdd
  pols = []
  for _ in range(numOdd):
    pols.append(randint(1, 2) * 2 + 1)
  for _ in range(numEven):
    pols.append(randint(1, 3) * 2)
  return pols


def checkPolygons():
  numOdd = 0
  for p in polygons:
    if p % 2 == 1:
      numOdd += 1
  assert numOdd % 2 == 1, 'there is odd number of polygons'


glues = set()
adjacency = []
vertices = []
polygons = []
vertexClasses = []
freeEdges = set()
edges = []
halfEdges = []
classes = []
gluedHalfEdges = []
cutVers = []
operations = []
bestAnswer = -1


def fillVertices():
  global vertices, vertexClasses, classes
  pols = []
  for i in range(len(polygons)):
    pols += [i] * polygons[i]
  vers = [i for i in range(len(pols))]
  vertices = list(zip(vers, pols))
  vertexClasses = [[i] for i in range(len(vertices))]
  classes = [i for i in range(len(vertices))]


def fillPolygons(pols):
  global polygons
  polygons = pols


def fillEdges():
  global adjacency, freeEdges, edges, halfEdges, gluedHalfEdges
  n = len(vertices)
  edges = []
  halfEdges = []
  adjacency = [[] for _ in range(n)]
  firstVert = 0
  for i in range(n):
    vert, pol = vertices[i]
    nextVert, nextPol = vertices[(i + 1) % n]
    edgeHead = 2 * i
    edgeTail = 2 * i + 1
    if nextPol != pol:
      adjacency[vert].append(edgeHead)
      adjacency[firstVert].append(edgeTail)
      edges.append((edgeHead, edgeTail))
      assert len(halfEdges) == edgeHead
      halfEdges.append(vert)
      assert len(halfEdges) == edgeTail
      halfEdges.append(firstVert)
      firstVert = nextVert
    else:
      adjacency[vert].append(edgeHead)
      adjacency[nextVert].append(edgeTail)
      edges.append((edgeHead, edgeTail))
      assert len(halfEdges) == edgeHead
      halfEdges.append(vert)
      assert len(halfEdges) == edgeTail
      halfEdges.append(nextVert)

  gluedHalfEdges = [-1 for i in range(2 * n)]
  freeEdges = set([i for i in range(n)])
  assert len(freeEdges) % 2 == 0, 'number of adjacency is odd'
  for i in range(n):
    assert len(adjacency[i]) == 2, '%d vertex has %d adjacency' % (i, len(adjacency[i]))


def cutGlues(e1, e2, e3, e4):
  global glues, freeEdges, vertexClasses, adjacency, gluedHalfEdges, cutVers
  glues.remove((e1, e2))
  # glues.remove((e2, e1))
  freeEdges.add(e1)
  freeEdges.add(e2)
  freeEdges.add(e3)
  freeEdges.add(e4)
  he1h, he1t = edges[e1]
  he2h, he2t = edges[e2]
  he3h, he3t = edges[e3]
  he4h, he4t = edges[e4]
  v1h, v1t = halfEdges[he1h], halfEdges[he1t]
  v2h, v2t = halfEdges[he2h], halfEdges[he2t]
  v3h, v3t = halfEdges[he3h], halfEdges[he3t]
  v4h, v4t = halfEdges[he4h], halfEdges[he4t]
  # print('   1 edge --', v1h, v1t)
  # print('   2 edge --', v2h, v2t)
  # !!!!!!!!!!!!!!! type of elements of vertexClasses
  cutVers = [v1h, v1t, v2h, v2t, v3h, v3t, v4h, v4t]
  
  gluedHalfEdges[he1h] = -1
  gluedHalfEdges[he2t] = -1
  gluedHalfEdges[he3h] = -1
  gluedHalfEdges[he4t] = -1
  gluedHalfEdges[he2h] = -1
  gluedHalfEdges[he1t] = -1
  gluedHalfEdges[he4h] = -1
  gluedHalfEdges[he3t] = -1
  
  vertexClasses[v1h].remove(v2t)
  vertexClasses[v2t].remove(v1h)
  
  vertexClasses[v3h].remove(v4t)
  vertexClasses[v4t].remove(v3h)
  
  vertexClasses[v2h].remove(v1t)
  vertexClasses[v1t].remove(v2h)

  vertexClasses[v4h].remove(v3t)
  vertexClasses[v3t].remove(v4h)
  
  changeOrder()
  # print('gluedHalfEdges =', [i for i in enumerate(gluedHalfEdges)])
  cutVertexClasses(v1h, v2t)
  cutVertexClasses(v2h, v1t)
  cutVertexClasses(v3h, v4t)
  cutVertexClasses(v4h, v3t)


def changeOrder():
  global adjacency, cutVers
  # print('cutVers =', cutVers)
  setVers = set(cutVers)
  cutVersCounter = Counter(cutVers)
  separVers = list(set(v for v in setVers if cutVersCounter[v] > 1))
  print('separVers =', separVers)
  if len(separVers) == 0:
    return  
  for v in separVers:
    # v = separVers[0]  
    st = edges[v][0]
    i1 = adjacency[v].index(st)
    i2 = i1 + 1
    # print('!!! v =', v, adjacency[v])
    while gluedHalfEdges[adjacency[v][i2 % len(adjacency[v])]] != -1:
      i2 += 1
    if i1 < i2:
      a1 = adjacency[v][i1 : i2 + 1].copy()
      a2 = adjacency[v][i2 + 1 :].copy() + adjacency[v][ : i1].copy()
    else:
      a1 = adjacency[v][i1 + 1 :].copy() + adjacency[v][ : i2].copy()
      a2 = adjacency[v][i2 : i1 + 1].copy()
    print('new orders are', a1, a2)
    adjacency[v] = a1.copy()
    for same in vertexClasses[separVers[0]]:
      adjacency[same] = a2.copy()
  cutVers = []


def glueEdge(e1, e2):
  global glues, freeEdges, vertexClasses, adjacency, gluedHalfEdges
  glues.add((e1, e2))
  # glues.add((e2, e1))
  freeEdges.remove(e1)
  freeEdges.remove(e2)
  he1h, he1t = edges[e1]
  he2h, he2t = edges[e2]
  v1h, v1t = halfEdges[he1h], halfEdges[he1t]
  v2h, v2t = halfEdges[he2h], halfEdges[he2t]
  # print('   1 edge --', v1h, v1t)
  # print('   2 edge --', v2h, v2t)
  vertexClasses[v1h].append(v2t)
  vertexClasses[v2t].append(v1h)
  # oldv2t = classes[v2t]
  if classes[v2t] != classes[v1h]:
    print('for vertices', v2t, 'and', v1h, 'diff classes =', classes)
    joinVertices(v1h, v2t, he1h, he2t)
    # print('new adjacency[] aka a2 =', a2)
    print('classes after =', classes)
    assert classes[v2t] == classes[v1h]
    for i in range(len(classes)):
      if classes[i] == classes[v2t]:
        adjacency[i] = adjacency[v2t].copy()


  vertexClasses[v2h].append(v1t)
  vertexClasses[v1t].append(v2h)
  if classes[v1t] != classes[v2h]:
    print('for vertices', v1t, 'and', v2h, 'diff classes =', classes)
    joinVertices(v2h, v1t, he2h, he1t)
    # print('new adjacency[] aka a2 =', a2)
    print('classes after joining =', classes)
    assert classes[v1t] == classes[v2h]
    for i in range(len(classes)):
      if classes[i] == classes[v1t]:
        adjacency[i] = adjacency[v1t].copy()

  gluedHalfEdges[he1h] = he2t
  gluedHalfEdges[he2t] = he1h
  gluedHalfEdges[he2h] = he1t
  gluedHalfEdges[he1t] = he2h 
  # print('gluedHalfEdges =', [i for i in enumerate(gluedHalfEdges)])
  # adjacency[v1h].insert(adjacency[v1h].index(he1h), he2t)
  # adjacency[v2h].insert(adjacency[v2h].index(he2h), he1t)
  # adjacency[v1t].insert(adjacency[v1t].index(he1t) + 1, he2h)
  # adjacency[v2t].insert(adjacency[v2t].index(he2t) + 1, he1h)
  # print('   glue', he1h, 'and', he2t)
  # print('   glue', he2t, 'and', he1h)
  # print('   glue', he2h, 'and', he1t)
  # print('   glue', he1t, 'and', he2h)


def joinVertices(v1, v2, he1, he2):
  def cycShift(a, ind):
    return a[ind :] + a[: ind]
  
  res = []
  print('    for vertex', v1, ', adjacency =', adjacency[v1])
  print('    for vertex', v2, ', adjacency =', adjacency[v2])
  i1 = adjacency[v1].index(he1)
  i2 = adjacency[v2].index(he2)
  # print('res in joinVertices =', res)
  res += cycShift(adjacency[v1], i1)
  # print('res in joinVertices =', res)
  res += cycShift(adjacency[v2], i2 + 1)
  # print('res in joinVertices =', res)
  joinVertexClasses(v1, v2, res)
  # return res


def randomGluing():
  global freeEdges
  while len(freeEdges) > 0:
    i = sample(freeEdges, 1)[0]
    j = sample(freeEdges, 1)[0]
    if i == j:
      continue
    print('glue', min(i, j), 'and', max(i, j))
    glueEdge(min(i, j), max(i, j))
    print('adjacency')
    for ppp in range(len(adjacency)):
      print(' ', ppp, '->', adjacency[ppp])
  

def setGluing():
  pairs = [(2, 10), (3, 7), (0, 9), (1, 11), (4, 6), (5, 8)]
  # pairs = [(0, 3), (1, 5), (2, 4)]
  for e1, e2 in pairs:
    print('glue', e1, 'edge and', e2, 'edge')
    glueEdge(e1, e2)
    print('adjacency')
    for i in range(len(adjacency)):
      print(' ', i, 'v. ->', adjacency[i])
  # cutGlues(1, 11, 4, 6)
  # glueEdge(6, 11)
  # glueEdge(1, 4)
  # cutGlues(0, 9, 5, 8)
  # glueEdge(8, 9)
  # glueEdge(0, 5)
  # cutGlues(2, 10, 3, 7)
  # glueEdge(2, 3)
  # glueEdge(7, 10)
  # best one!


def randomDcj():
  global operations, bestAnswer
  opers = []
  current = -1
  # cutGlues(4, 6, 5, 8)
  # glueEdge(6, 8)
  # glueEdge(4, 5)

  # cutGlues(1, 11, 0, 9)
  # glueEdge(9, 11)
  # glueEdge(0, 1)

  # cutGlues(2, 10, 3, 7)
  # glueEdge()
  while True:
    print()
    e = sample(glues, 1)[0]
    r = sample(glues, 1)[0]
    if e[0] == r[0]:
      continue
    i = randint(0, 1)
    print('classes before cut =', classes)
    print('cut', e[0], e[1])
    print('cut', r[0], r[1])
    cutGlues(e[0], e[1], r[0], r[1])
    print('     classes =', classes)
    
    print('adjacency after cut:')
    for ppp in range(len(adjacency)):
      print(' ', ppp, '->', adjacency[ppp])


    print('glue', e[0], r[i])
    glueEdge(e[0], r[i])
    print('     classes =', classes)
    print('glue', e[1], r[1 - i])
    glueEdge(e[1], r[1 - i])
    print('     classes =', classes)
    print()
    before = (e, r)
    firstGlue = (e[0], r[i])
    secondGlue = (e[1], r[1 - i])
    opers += [before, (firstGlue, secondGlue)]

    print('adjacency after dcj:')
    for ppp in range(len(adjacency)):
      print(' ', ppp, '->', adjacency[ppp])

  #   # opers.append(((e, r), ((e[0], r[i]), (e[1], r[1 - i]))))
  #   print('classes =', classes)
    current = len(set(classes))
    if current > bestAnswer:
      operations = opers
      bestAnswer = current
      print('  new answer is', current)
      print('  opers for this answer are  ', *opers)
    if bestAnswer == 7:
      break


def cutVertexClasses(v1, v2):
  global classes
  newClass = max(classes) + 1

  def fillNewClasses(v):
    classes[v] = newClass
    for ch in vertexClasses[v]:
      if classes[ch] != newClass:
        fillNewClasses(ch)
  
  fillNewClasses(v2)
  # print('!!!!!!!!!!!!!!!!!!!!!', classes)
  

def joinVertexClasses(v1, v2, newAdj):
  global classes
  # numberClasses = 0
  # firstFree = 0
  # classes[0] = 0
  oldV1 = classes[v1]
  oldV2 = classes[v2]
  # print('  before join classes =', classes)
  for i in range(len(classes)):
    if classes[i] == oldV2 or classes[i] == oldV1:
      # print('true for', i)
      classes[i] = oldV1
      adjacency[i] = newAdj.copy()
  print('  join classes for', v1, 'and', v2, 'vertices, classes =', classes)
  print('adjacency after joining classes:')
  for ppp in range(len(adjacency)):
    print(' ', ppp, '->', adjacency[ppp])

  # while True:
  #   classes[firstFree] = numberClasses
  #   changed = True
  #   # print('classes =', list(zip(classes, [i for i in range(len(classes))])))
  #   while changed:
  #     changed = False
  #     for i in range(len(vertexClasses)):
  #       if classes[i] == numberClasses:
  #         for j in range(len(vertexClasses[i])):
  #           if classes[vertexClasses[i][j]] != numberClasses:
  #             classes[vertexClasses[i][j]] = numberClasses
  #             changed = True
  #             # print('classes =', list(zip(classes, [i for i in range(len(classes))])))
  #   numberClasses += 1
  #   for i in range(len(classes)):
  #     if classes[i] == -1:
  #       firstFree = i
  #       break
  #   else:
  #     break
  # return numberClasses


def solve():
  # fillPolygons(genPolygons(3))
  fillPolygons([3, 3, 6])
  # fillPolygons([6])
  fillVertices()
  # while True:
  fillEdges()
  # print('adjacency =', *adjacency)
  # randomGluing()
  setGluing()
  print('-' * 30)
  print()
  # print(classes)
  randomDcj()
  print('-' * 30)
  # print('edges =', *edges)
  # print('glues =', *glues)
  # for i in range(len(vertexClasses)):
    # print(i, '->', vertexClasses[i])
  # classes, numberClasses = joinVertexClasses()
  # print('classes =', classes)
  # print('adjacency')
  # for i in range(len(adjacency)):
  #   print(' ', i, '->', adjacency[i])
  # # print('numberClasses =', numberClasses)
  # print()
  # print(*polygons)
  # print(*vertices)
  # for i in range(len(adjacency)):
  #   print(i, '->', adjacency[i])


if __name__ == '__main__':
  solve()