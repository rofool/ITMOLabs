import sys

sys.setrecursionlimit(10000)


class Node:
    __slots__ = ('key', 'left', 'right')

    def __init__(self, key):
        self.key = key
        self.left = self.right = None


def height(v):
    if v is None:
        return 0
    return 1 + max(height(v.left), height(v.right))


def add(v, x):
    if v is None:
        return Node(x)
    if x < v.key:
        v.left = add(v.left, x)
    elif x > v.key:
        v.right = add(v.right, x)
    else:
        return v
    return v


def delete(v, x):
    if v is None:
        return None
    if x < v.key:
        v.left = delete(v.left, x)
    elif x > v.key:
        v.right = delete(v.right, x)
    else:
        if v.left is None:
            return v.right
        if v.right is None:
            return v.left
        successor = v.right
        while successor.left is not None:
            successor = successor.left
        v.key = successor.key
        v.right = delete(v.right, successor.key)
    return v


def exists(v, x):
    while v is not None:
        if x == v.key:
            return True
        v = v.left if x < v.key else v.right
    return False


def preorder(root):
    if root is None:
        return 'Empty'
    result = []
    stack = [root]
    while stack:
        v = stack.pop()
        result.append(str(v.key))
        if v.right is not None:
            stack.append(v.right)
        if v.left is not None:
            stack.append(v.left)
    return ' '.join(result)


def main():
    input = sys.stdin.buffer.readline
    q = int(input())
    root = None
    answer = []
    for _ in range(q):
        parts = input().split()
        command = parts[0]
        if command == b'add':
            root = add(root, int(parts[1]))
        elif command == b'del':
            root = delete(root, int(parts[1]))
        elif command == b'exists':
            answer.append('YES' if exists(root, int(parts[1])) else 'NO')
        elif command == b'height':
            answer.append(str(height(root)))
        elif command == b'print':
            answer.append(preorder(root))
    sys.stdout.write('\n'.join(answer))


if __name__ == '__main__':
    main()
